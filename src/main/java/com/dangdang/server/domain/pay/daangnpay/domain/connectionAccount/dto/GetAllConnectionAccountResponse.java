package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

import java.util.List;

public record GetAllConnectionAccountResponse(List<AllConnectionAccount> allConnectionAccounts) {

  public static GetAllConnectionAccountResponse from(
      List<AllConnectionAccount> allConnectionAccounts) {
    return new GetAllConnectionAccountResponse(allConnectionAccounts);
  }
}
