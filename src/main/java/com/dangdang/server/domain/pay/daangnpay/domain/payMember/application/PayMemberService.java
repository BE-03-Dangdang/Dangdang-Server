package com.dangdang.server.domain.pay.daangnpay.domain.payMember.application;

import static com.dangdang.server.domain.pay.daangnpay.global.vo.TrustAccount.OPEN_BANKING_CONTRACT_ACCOUNT;
import static com.dangdang.server.global.exception.ExceptionCode.CHARGE_LESS_THAN_MIN_AMOUNT;
import static com.dangdang.server.global.exception.ExceptionCode.PAY_MEMBER_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.WITHDRAW_LESS_THAN_MIN_AMOUNT;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetConnectionAccountReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.FeeInfo;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayMemberRepository;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
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
   * 당근머니 충전
   */
  @Transactional
  public PayResponse charge(Long memberId, PayRequest payRequest) {
    if (!PayType.CHARGE.checkMinAmount(payRequest.amount())) {
      throw new MinAmountException(CHARGE_LESS_THAN_MIN_AMOUNT);
    }

    PayMember payMember = getPayMember(memberId);
    String fintechUseNum = connectionAccountDatabaseService.getFintechUseNum(
        payRequest.bankAccountNumber());
    OpenBankingWithdrawRequest openBankingWithdrawRequest = createOpenBankingWithdrawRequest(
        payMember.getId(), payRequest, fintechUseNum);
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
    OpenBankingDepositRequest openBankingDepositRequestFromWithdraw = createOpenBankingDepositRequest(
        payMember.getId(), payRequest);

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

    OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest = createOpenBankingInquiryReceiveRequest(
        payMemberId, receiveRequest);
    OpenBankingInquiryReceiveResponse openBankingInquiryReceiveResponse = openBankingService.inquiryReceive(
        openBankingInquiryReceiveRequest);

    GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse = connectionAccountDatabaseService.findIsMyAccountAndChargeAccountByReceiveRequest(
        payMemberId, receiveRequest);

    int autoChargeAmount = payMember.calculateAutoChargeAmount(receiveRequest.depositAmount());
    FeeInfo feeInfo = payMember.getFeeInfo();
    return ReceiveResponse.of(openBankingInquiryReceiveResponse,
        getConnectionAccountReceiveResponse, autoChargeAmount, feeInfo);
  }

  private PayMember getPayMember(Long memberId) {
    return payMemberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new EmptyResultException(PAY_MEMBER_NOT_FOUND));
  }

  private OpenBankingWithdrawRequest createOpenBankingWithdrawRequest(Long payMemberId,
      PayRequest payRequest, String fintechUseNum) {
    return new OpenBankingWithdrawRequest(payMemberId, payRequest.openBankingToken(),
        fintechUseNum, OPEN_BANKING_CONTRACT_ACCOUNT.getAccountNumber(),
        payRequest.bankAccountNumber(), payRequest.amount());
  }

  private OpenBankingDepositRequest createOpenBankingDepositRequest(Long payMemberId,
      PayRequest payRequest) {
    return new OpenBankingDepositRequest(payMemberId, payRequest.openBankingToken(),
        payRequest.bankAccountNumber(), OPEN_BANKING_CONTRACT_ACCOUNT.getAccountNumber(),
        payRequest.amount());
  }

  private OpenBankingInquiryReceiveRequest createOpenBankingInquiryReceiveRequest(Long payMemberId,
      ReceiveRequest receiveRequest) {
    return new OpenBankingInquiryReceiveRequest(payMemberId, receiveRequest.openBankingToken(),
        receiveRequest.bankAccountNumber(), receiveRequest.bankCode());
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