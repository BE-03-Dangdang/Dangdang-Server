package com.dangdang.server.domain.pay.banks.bankAccount;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_NOT_FOUND;

import com.dangdang.server.domain.pay.banks.bankAccount.domain.BankAccountRepository;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.banks.bankAccount.dto.BankOpenBankingApiResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.exception.EmptyResultException;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingInquiryReceiveRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
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

  public BankAccount findByAccountNumber(String bankAccountNumber) {
    return bankAccountRepository.findByAccountNumber(bankAccountNumber)
        .orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public BankOpenBankingApiResponse withdraw(
      OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    String fromBankAccountNumber = openBankingWithdrawRequest.fromBankAccountNumber();

    BankAccount bankAccount = findByAccountNumber(fromBankAccountNumber);
    bankAccount.withdraw(openBankingWithdrawRequest);

    return BankOpenBankingApiResponse.from(bankAccount);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public BankOpenBankingApiResponse deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    String toBankAccountNumber = openBankingDepositRequest.toBankAccountNumber();

    BankAccount bankAccount = findByAccountNumber(toBankAccountNumber);
    bankAccount.deposit(openBankingDepositRequest);

    return BankOpenBankingApiResponse.from(bankAccount);
  }

  public BankOpenBankingApiResponse inquiryReceive(
      OpenBankingInquiryReceiveRequest openBankingInquiryReceiveRequest) {
    BankAccount bankAccount = bankAccountRepository.findByPayMemberIdAndAccountNumber(
            openBankingInquiryReceiveRequest.payMemberId(),
            openBankingInquiryReceiveRequest.depositBankAccountNumber())
        .orElseThrow(() -> new EmptyResultException(BANK_ACCOUNT_NOT_FOUND));
    return BankOpenBankingApiResponse.from(bankAccount);
  }
}
