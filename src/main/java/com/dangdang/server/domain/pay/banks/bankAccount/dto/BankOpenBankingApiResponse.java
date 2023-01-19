package com.dangdang.server.domain.pay.banks.bankAccount.dto;

import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;

public record BankOpenBankingApiResponse(String bank, String accountNumber) {

  public static BankOpenBankingApiResponse from(BankAccount bankAccount) {
    return new BankOpenBankingApiResponse(bankAccount.getBank(), bankAccount.getAccountNumber());
  }
}
