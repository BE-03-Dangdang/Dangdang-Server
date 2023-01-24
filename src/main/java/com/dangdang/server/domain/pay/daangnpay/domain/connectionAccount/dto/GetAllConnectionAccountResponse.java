package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity.ConnectionAccount;

public record GetAllConnectionAccountResponse(String bankName, String connectionAccountNumber) {

  public static GetAllConnectionAccountResponse from(ConnectionAccount connectionAccount) {
    return new GetAllConnectionAccountResponse(connectionAccount.getBank(),
        connectionAccount.getBankAccountNumber());
  }
}
