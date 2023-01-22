package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class AddConnectionAccountRequest {

  private Long bankAccountId;

  @JsonCreator
  public AddConnectionAccountRequest(Long bankAccountId) {
    this.bankAccountId = bankAccountId;
  }

}
