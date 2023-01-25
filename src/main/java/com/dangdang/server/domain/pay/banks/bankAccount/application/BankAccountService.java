package com.dangdang.server.domain.pay.banks.bankAccount.application;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_AUTHENTICATION_FAIL;
import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_INACTIVE;
import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.INSUFFICIENT_BALANCE;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.BankAccountAuthenticationException;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.InsufficientBankAccountException;
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
    Long payMemberId = openBankingWithdrawRequest.payMemberId();
    Long fromBankAccountId = openBankingWithdrawRequest.fromBankAccountId();

    BankAccount bankAccount = findById(fromBankAccountId);
    checkBankAccountStatus(bankAccount);
    checkPayMemberId(bankAccount, payMemberId);
    checkBalance(bankAccount, openBankingWithdrawRequest);

    int amount = openBankingWithdrawRequest.amount();
    bankAccount.withdraw(amount);

    return BankOpenBankingApiResponse.from(bankAccount);
  }

  @Transactional
  public BankOpenBankingApiResponse deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    Long toBankAccountId = openBankingDepositRequest.toBankAccountId();

    BankAccount bankAccount = findById(toBankAccountId);
    checkBankAccountStatus(bankAccount);
    bankAccount.deposit(openBankingDepositRequest.amount());

    return BankOpenBankingApiResponse.from(bankAccount);
  }

  private void checkBankAccountStatus(BankAccount bankAccount) {
    if (bankAccount.getStatus() == StatusType.INACTIVE) {
      throw new InactiveBankAccountException(BANK_ACCOUNT_INACTIVE);
    }
  }

  public Long getPayMemberIdFromAccount(BankAccount bankAccount) {
    return bankAccount.getPayMember().getId();
  }

  private void checkPayMemberId(BankAccount bankAccount, Long payMemberId) {
    Long payMemberIdFromAccount = getPayMemberIdFromAccount(bankAccount);
    if (!payMemberIdFromAccount.equals(payMemberId)) {
      throw new BankAccountAuthenticationException(BANK_ACCOUNT_AUTHENTICATION_FAIL);
    }
  }

  private void checkBalance(BankAccount bankAccount,
      OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    if (bankAccount.getBalance() < openBankingWithdrawRequest.amount()) {
      throw new InsufficientBankAccountException(INSUFFICIENT_BALANCE);
    }
  }
}
