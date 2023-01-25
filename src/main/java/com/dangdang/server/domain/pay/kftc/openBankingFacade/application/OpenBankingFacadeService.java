package com.dangdang.server.domain.pay.kftc.openBankingFacade.application;

import com.dangdang.server.domain.pay.banks.bankAccount.application.BankAccountService;
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
   * 입금 이체 미완성
   */
  @Transactional
  public void deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    trustAccountService.withdraw(openBankingDepositRequest);
    bankAccountService.deposit(openBankingDepositRequest);
  }

  /**
   * 출금 이체
   */
  @Transactional
  public OpenBankingResponse withdraw(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    BankOpenBankingApiResponse bankOpenBankingApiResponse = bankAccountService.withdraw(
        openBankingWithdrawRequest);
    log.info("오픈뱅킹 출금 완료");
    trustAccountService.deposit(openBankingWithdrawRequest);

    return OpenBankingResponse.of(openBankingWithdrawRequest.payMemberId(),
        bankOpenBankingApiResponse, LocalDateTime.now());
  }

}
