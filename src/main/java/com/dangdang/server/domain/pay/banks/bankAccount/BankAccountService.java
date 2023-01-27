package com.dangdang.server.domain.pay.banks.bankAccount;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_NOT_FOUND;

import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.openBankingFacade.dto.OpenBankingWithdrawRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BankAccountService {

  private final BankAccountRepository bankAccountRepository;

  public BankAccountService(BankAccountRepository bankAccountRepository) {
    this.bankAccountRepository = bankAccountRepository;
  }

  public BankAccount findById(Long bankAccountId) {
    return bankAccountRepository.findById(bankAccountId)
        .orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public BankOpenBankingApiResponse withdraw(
      OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    Long fromBankAccountId = openBankingWithdrawRequest.fromBankAccountId();

    BankAccount bankAccount = findById(fromBankAccountId);
    bankAccount.withdraw(openBankingWithdrawRequest);

    return BankOpenBankingApiResponse.from(bankAccount);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public BankOpenBankingApiResponse deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    Long toBankAccountId = openBankingDepositRequest.toBankAccountId();

    BankAccount bankAccount = findById(toBankAccountId);
    bankAccount.deposit(openBankingDepositRequest);

    return BankOpenBankingApiResponse.from(bankAccount);
  }

}
