package com.dangdang.server.domain.pay.banks.trustAccount.application;

import static com.dangdang.server.global.exception.ExceptionCode.TRUST_ACCOUNT_NOT_FOUND;

import com.dangdang.server.domain.pay.banks.trustAccount.domain.TrustAccountRepository;
import com.dangdang.server.domain.pay.banks.trustAccount.domain.entity.TrustAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TrustAccountService {

  private final TrustAccountRepository trustAccountRepository;

  public TrustAccountService(TrustAccountRepository trustAccountRepository) {
    this.trustAccountRepository = trustAccountRepository;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void deposit(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    String toTrustAccountNumber = openBankingWithdrawRequest.toTrustAccountNumber();
    TrustAccount trustAccount = trustAccountRepository.findByAccountNumber(toTrustAccountNumber)
        .orElseThrow(() -> new EmptyResultException(TRUST_ACCOUNT_NOT_FOUND));

    trustAccount.deposit(openBankingWithdrawRequest);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void withdraw(OpenBankingDepositRequest openBankingDepositRequest) {
    String toTrustAccountNumber = openBankingDepositRequest.fromTrustAccountNumber();
    TrustAccount trustAccount = trustAccountRepository.findByAccountNumber(toTrustAccountNumber)
        .orElseThrow(() -> new EmptyResultException(TRUST_ACCOUNT_NOT_FOUND));

    trustAccount.withdraw(openBankingDepositRequest);
  }
}
