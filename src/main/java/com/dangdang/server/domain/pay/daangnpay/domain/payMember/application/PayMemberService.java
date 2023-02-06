package com.dangdang.server.domain.pay.daangnpay.domain.payMember.application;

import static com.dangdang.server.domain.pay.daangnpay.global.vo.TrustAccount.OPEN_BANKING_CONTRACT_ACCOUNT;
import static com.dangdang.server.global.exception.ExceptionCode.CHARGE_LESS_THAN_MIN_AMOUNT;
import static com.dangdang.server.global.exception.ExceptionCode.PAY_MEMBER_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.WITHDRAW_LESS_THAN_MIN_AMOUNT;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetConnectionAccountReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.FeeInfo;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberSignupResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.RemittanceRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.RemittanceResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.MinAmountException;
import com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.application.PayUsageHistoryService;
import com.dangdang.server.domain.pay.kftc.OpenBankingService;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveResponse;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingResponse;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetUserMeResponse;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayMemberService {

  private final OpenBankingService openBankingService;
  private final PayUsageHistoryService payUsageHistoryService;
  private final ConnectionAccountDatabaseService connectionAccountDatabaseService;
  private final PayMemberRepository payMemberRepository;

  public PayMemberService(OpenBankingService openBankingService,
      PayUsageHistoryService payUsageHistoryService,
      ConnectionAccountDatabaseService connectionAccountDatabaseService,
      PayMemberRepository payMemberRepository) {
    this.openBankingService = openBankingService;
    this.payUsageHistoryService = payUsageHistoryService;
    this.connectionAccountDatabaseService = connectionAccountDatabaseService;
    this.payMemberRepository = payMemberRepository;
  }

  public void createOpenBankingMemberFromState(PostPayMemberRequest postPayMemberRequest) {
    Long memberId = postPayMemberRequest.memberId();
    PayMember payMember = getPayMember(memberId);
    openBankingService.createOpenBankingMemberFromState(postPayMemberRequest.state(), payMember);
  }

  /**
   * openAPI token
   */
  public GetAuthTokenResponse getAuthTokenResponse(GetAuthTokenRequest getAuthTokenRequest) {
    return openBankingService.getAuthToken(getAuthTokenRequest);
  }

  /**
   * openAPI 사용자 정보 조회
   */
  public GetUserMeResponse getUserMeResponse(String token, String user_seq_no) {
    return openBankingService.getUserMeResponse(token, user_seq_no);
  }

  /**
   * 당근페이 가입
   */
  public PostPayMemberSignupResponse signup(String password, Long memberId) {
    PayMember payMember = new PayMember(password, memberId);
    payMember = payMemberRepository.save(payMember);
    return PostPayMemberSignupResponse.from(payMember.getId());
  }

  /**
   * 당근머니 충전
   */
  @Transactional
  public PayResponse charge(Long memberId, PayRequest payRequest) {
    if (!PayType.CHARGE.checkMinAmount(payRequest.amount())) {
      throw new MinAmountException(CHARGE_LESS_THAN_MIN_AMOUNT);
    }

    PayMember payMember = getPayMember(memberId);
    ConnectionAccount connectionAccount = connectionAccountDatabaseService.findByAccountNumber(
        payRequest.bankAccountNumber());
    OpenBankingWithdrawRequest openBankingWithdrawRequest = createOpenBankingWithdrawRequest(
        payMember, payRequest, connectionAccount);
    OpenBankingResponse openBankingResponse = openBankingService.withdraw(
        openBankingWithdrawRequest);

    int balanceMoney = addPayMemberMoney(payMember, payRequest);

    payUsageHistoryService.addUsageHistory(PayType.CHARGE, openBankingResponse, balanceMoney,
        payMember);

    return PayResponse.from(openBankingResponse, balanceMoney);
  }

  /**
   * 당근머니 출금
   */
  @Transactional
  public PayResponse withdraw(Long memberId, PayRequest payRequest) {
    if (!PayType.WITHDRAW.checkMinAmount(payRequest.amount())) {
      throw new MinAmountException(WITHDRAW_LESS_THAN_MIN_AMOUNT);
    }

    PayMember payMember = getPayMember(memberId);
    return depositRequestLogic(payMember, payRequest, PayType.WITHDRAW);
  }

  /**
   * 입금 이체 요청
   */
  @Transactional
  public PayResponse depositRequestLogic(PayMember payMember, PayRequest payRequest,
      PayType payType) {
    ConnectionAccount connectionAccount = null;
    if (payType == PayType.WITHDRAW) {
      connectionAccount = connectionAccountDatabaseService.findByAccountNumber(
          payRequest.bankAccountNumber());
    }

    if (payType == PayType.REMITTANCE) {
      connectionAccount = connectionAccountDatabaseService.findMainConnectionAccountByPayMember(
          payMember);
    }

    OpenBankingDepositRequest openBankingDepositRequestFromWithdraw = createOpenBankingDepositRequest(
        payMember, payRequest, Objects.requireNonNull(connectionAccount));

    OpenBankingResponse openBankingResponse = openBankingService.deposit(
        openBankingDepositRequestFromWithdraw);

    int balanceMoney = minusPayMemberMoney(payMember, payRequest);

    payUsageHistoryService.addUsageHistory(PayType.WITHDRAW, openBankingResponse, balanceMoney,
        payMember);

    return PayResponse.from(openBankingResponse, balanceMoney);
  }

  // TODO : 비밀번호 입력 로직 추가

  /**
   * 수취 조회
   */
  public ReceiveResponse inquiryReceive(Long memberId, ReceiveRequest receiveRequest) {
    PayMember payMember = getPayMember(memberId);
    Long payMemberId = payMember.getId();

    GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse = connectionAccountDatabaseService.findIsMyAccountAndChargeAccount(
        payMemberId, receiveRequest.bankAccountNumber());

    OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest = createOpenBankingInquiryReceiveRequest(
        payMember, receiveRequest, getConnectionAccountReceiveResponse);
    OpenBankingInquiryReceiveResponse openBankingInquiryReceiveResponse = openBankingService.inquiryReceive(
        openBankingInquiryReceiveRequest);

    int autoChargeAmount = payMember.calculateAutoChargeAmount(receiveRequest.depositAmount());
    FeeInfo feeInfo = payMember.getFeeInfo();
    return ReceiveResponse.of(openBankingInquiryReceiveResponse,
        getConnectionAccountReceiveResponse, autoChargeAmount, feeInfo);
  }

  /**
   * 당근머니 송금
   */
  @Transactional
  public RemittanceResponse remittance(Long memberId, RemittanceRequest remittanceRequest) {
    PayMember payMember = getPayMember(memberId);
    Long payMemberId = payMember.getId();

    GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse = connectionAccountDatabaseService.findIsMyAccountAndChargeAccount(
        payMemberId, remittanceRequest.bankAccountNumber());

    int autoChargeAmount = payMember.calculateAutoChargeAmount(remittanceRequest.depositAmount());

    // 자동충전 금액 출금이체 요청
    ConnectionAccount chargeAccount = connectionAccountDatabaseService.findMainConnectionAccountByPayMember(
        payMember);
    OpenBankingWithdrawRequest openBankingWithdrawRequest = createOpenBankingWithdrawRequest(
        payMember, new PayRequest(remittanceRequest.openBankingToken(), chargeAccount.getBank(),
            chargeAccount.getBankAccountNumber(), autoChargeAmount), chargeAccount);
    openBankingService.withdraw(openBankingWithdrawRequest);

    payMember.addMoney(autoChargeAmount);

    FeeInfo feeInfo = payMember.getFeeInfo();
    payMember.minusMoney(feeInfo.getFeeAmount());

    PayResponse payResponse = depositRequestLogic(payMember,
        new PayRequest(remittanceRequest.openBankingToken(), remittanceRequest.bankName(),
            remittanceRequest.bankAccountNumber(), remittanceRequest.depositAmount()),
        PayType.REMITTANCE);

    return RemittanceResponse.of(getConnectionAccountReceiveResponse, autoChargeAmount, feeInfo,
        payResponse);
  }

  private PayMember getPayMember(Long memberId) {
    return payMemberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new EmptyResultException(PAY_MEMBER_NOT_FOUND));
  }

  private OpenBankingWithdrawRequest createOpenBankingWithdrawRequest(PayMember payMember,
      PayRequest payRequest, ConnectionAccount connectionAccount) {
    return new OpenBankingWithdrawRequest(payMember.getId(), payRequest.openBankingToken(),
        connectionAccount.getFintechUseNum(), OPEN_BANKING_CONTRACT_ACCOUNT.getAccountNumber(),
        payMember.getName(), payRequest.bankAccountNumber(), payRequest.amount());
  }

  private OpenBankingDepositRequest createOpenBankingDepositRequest(PayMember payMember,
      PayRequest payRequest, ConnectionAccount connectionAccount) {
    return new OpenBankingDepositRequest(payMember.getId(), payRequest.openBankingToken(),
        connectionAccount.getFintechUseNum(), payMember.getName(),
        connectionAccount.getBank(), connectionAccount.getBankAccountNumber(),
        payRequest.bankName(), payRequest.bankAccountNumber(),
        OPEN_BANKING_CONTRACT_ACCOUNT.getAccountNumber(), payRequest.amount());
  }

  private OpenBankingInquiryReceiveRequest createOpenBankingInquiryReceiveRequest(
      PayMember payMember, ReceiveRequest receiveRequest,
      GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse) {
    return new OpenBankingInquiryReceiveRequest(payMember.getId(),
        receiveRequest.openBankingToken(),
        getConnectionAccountReceiveResponse.chargeAccountBankName(),
        payMember.getName(), getConnectionAccountReceiveResponse.chargeAccountNumber(),
        receiveRequest.bankName(), receiveRequest.bankAccountNumber(),
        receiveRequest.depositAmount());
  }

  private int minusPayMemberMoney(PayMember payMember, PayRequest payRequest) {
    int amount = payRequest.amount();
    return payMember.minusMoney(amount);
  }

  private int addPayMemberMoney(PayMember payMember, PayRequest payRequest) {
    int amount = payRequest.amount();
    return payMember.addMoney(amount);
  }
}