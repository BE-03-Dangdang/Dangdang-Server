package com.dangdang.server.domain.pay.kftc.openBankingFacade.application;

import static com.dangdang.server.global.exception.ExceptionCode.NOT_SUPPORTED;

import com.dangdang.server.domain.pay.banks.bankAccount.BankAccountService;
import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.banks.trustAccount.application.TrustAccountService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.kftc.OpenBankingService;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveResponse;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingResponse;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenRequest;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetAuthTokenResponse;
import com.dangdang.server.domain.pay.kftc.feignClient.dto.GetUserMeResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.exception.NotSupportedFunctionException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("internal")
@Slf4j
@Service
@Transactional(readOnly = true)
public class OpenBankingFacadeService implements OpenBankingService {

  private final BankAccountService bankAccountService;
  private final TrustAccountService trustAccountService;

  public OpenBankingFacadeService(BankAccountService bankAccountService,
      TrustAccountService trustAccountService) {
    this.bankAccountService = bankAccountService;
    this.trustAccountService = trustAccountService;
  }

  /**
   * 입금 이체
   */
  @Transactional
  public OpenBankingResponse deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    trustAccountService.withdraw(openBankingDepositRequest);
    BankOpenBankingApiResponse bankOpenBankingApiResponse = bankAccountService.deposit(
        openBankingDepositRequest);

    return OpenBankingResponse.ofInternal(openBankingDepositRequest.payMemberId(),
        bankOpenBankingApiResponse, LocalDateTime.now());
  }

  /**
   * 출금 이체
   */
  @Transactional
  public OpenBankingResponse withdraw(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    BankOpenBankingApiResponse bankOpenBankingApiResponse = bankAccountService.withdraw(
        openBankingWithdrawRequest);
    trustAccountService.deposit(openBankingWithdrawRequest);

    return OpenBankingResponse.ofInternal(openBankingWithdrawRequest.payMemberId(),
        bankOpenBankingApiResponse, LocalDateTime.now());
  }

  /**
   * 수취 조회
   */
  public OpenBankingInquiryReceiveResponse inquiryReceive(
      OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest) {
    BankOpenBankingApiResponse bankOpenBankingApiREsponse = bankAccountService.inquiryReceive(
        openBankingInquiryReceiveRequest);

    return new OpenBankingInquiryReceiveResponse(openBankingInquiryReceiveRequest.payMemberId(),
        bankOpenBankingApiREsponse.bankName(), bankOpenBankingApiREsponse.clientName(),
        bankOpenBankingApiREsponse.accountNumber(), LocalDateTime.now());
  }

  @Override
  public void createOpenBankingMemberFromState(String state, PayMember payMember) {
    throw new NotSupportedFunctionException(NOT_SUPPORTED);
  }

  @Override
  public GetAuthTokenResponse getAuthToken(GetAuthTokenRequest getAuthTokenRequest) {
    throw new NotSupportedFunctionException(NOT_SUPPORTED);
  }

  @Override
  public GetUserMeResponse getUserMeResponse(String token, String user_seq_no) {
    throw new NotSupportedFunctionException(NOT_SUPPORTED);
  }

}
