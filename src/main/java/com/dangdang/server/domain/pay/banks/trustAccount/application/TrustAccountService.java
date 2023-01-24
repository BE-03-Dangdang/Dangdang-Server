package com.dangdang.server.domain.pay.banks.trustAccount.application;

import static com.dangdang.server.global.exception.ExceptionCode.TRUST_ACCOUNT_INACTIVE;
import static com.dangdang.server.global.exception.ExceptionCode.TRUST_ACCOUNT_NOT_FOUND;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.pay.banks.trustAccount.domain.TrustAccountRepository;
import com.dangdang.server.domain.pay.banks.trustAccount.domain.entity.TrustAccount;
import com.dangdang.server.domain.pay.banks.trustAccount.exception.InactiveTrustAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingWithdrawRequest;
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
    Long toTrustAccountId = openBankingWithdrawRequest.toTrustAccountId();
    TrustAccount trustAccount = trustAccountRepository.findById(toTrustAccountId)
        .orElseThrow(() -> new EmptyResultException(TRUST_ACCOUNT_NOT_FOUND));

    checkTrustAccountStatus(trustAccount);
    trustAccount.deposit(openBankingWithdrawRequest);
  }

  @Transactional
  public void withdraw(OpenBankingDepositRequest openBankingDepositRequest) {
    Long fromTrustAccountId = openBankingDepositRequest.fromTurstAccountId();
    TrustAccount trustAccount = trustAccountRepository.findById(fromTrustAccountId)
        .orElseThrow(() -> new EmptyResultException(TRUST_ACCOUNT_NOT_FOUND));

    checkTrustAccountStatus(trustAccount);
    trustAccount.withdraw(openBankingDepositRequest);
  }

  @Transactional
  public void checkTrustAccountStatus(TrustAccount trustAccount) {
    if (trustAccount.getStatus() == StatusType.INACTIVE) {
      throw new InactiveTrustAccountException(TRUST_ACCOUNT_INACTIVE);
    }
  }
}
