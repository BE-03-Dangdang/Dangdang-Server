package com.dangdang.server.domain.pay.banks.bankAccount.dto;

import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;

public record BankOpenBankingApiResponse(String bankName, String clientName, String accountNumber) {

  public static BankOpenBankingApiResponse from(BankAccount bankAccount) {
    return new BankOpenBankingApiResponse(bankAccount.getBankName(), bankAccount.getClientName(),
        bankAccount.getAccountNumber());
  }
}
