package com.dangdang.server.domain.pay.kftc.openBankingFacade;

import com.dangdang.server.domain.pay.banks.bankAccount.BankAccountService;
import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.banks.trustAccount.application.TrustAccountService;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingResponse;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingWithdrawRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OpenBankingFacadeService {

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

    return OpenBankingResponse.of(openBankingDepositRequest.payMemberId(),
        bankOpenBankingApiResponse, LocalDateTime.now());
  }

  /**
   * 출금 이체
   */
  @Transactional
  public OpenBankingResponse withdraw(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    BankOpenBankingApiResponse bankOpenBankingApiResponse = bankAccountService.withdraw(
        openBankingWithdrawRequest);
    log.info("오픈뱅킹 출금이체 : 출금 완료");
    trustAccountService.deposit(openBankingWithdrawRequest);

    return OpenBankingResponse.of(openBankingWithdrawRequest.payMemberId(),
        bankOpenBankingApiResponse, LocalDateTime.now());
  }

}