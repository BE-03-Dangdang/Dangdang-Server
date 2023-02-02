package com.dangdang.server.domain.pay.daangnpay.global.vo;

public enum TrustAccount {

  OPEN_BANKING_CONTRACT_ACCOUNT(1L, "200000000001", "신한은행");

  private final Long accountId;
  private final String accountNumber;
  private final String bank;

  TrustAccount(Long accountId, String accountNumber, String bank) {
    this.accountId = accountId;
    this.accountNumber = accountNumber;
    this.bank = bank;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
}
