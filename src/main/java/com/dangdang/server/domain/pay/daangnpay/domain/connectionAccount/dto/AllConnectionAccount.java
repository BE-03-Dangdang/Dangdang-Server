package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;

public record AllConnectionAccount(String bankName, String connectionAccountNumber) {

  public static AllConnectionAccount from(ConnectionAccount connectionAccount) {
    return new AllConnectionAccount(connectionAccount.getBank(),
        connectionAccount.getBankAccountNumber());
  }
}
