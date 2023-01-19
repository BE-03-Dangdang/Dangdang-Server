package com.dangdang.server.domain.pay.connectionAccount.dto;

import com.dangdang.server.domain.pay.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.connectionAccount.domain.entity.ConnectionAccount;
import com.dangdang.server.domain.pay.payMember.domain.entity.PayMember;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class AddConnectionAccountRequest {

  private Long bankAccountId;

  @JsonCreator
  public AddConnectionAccountRequest(Long bankAccountId) {
    this.bankAccountId = bankAccountId;
  }

  public static ConnectionAccount toConnectionAccount(PayMember payMember,
      BankAccount bankAccount) {

    return new ConnectionAccount(payMember, bankAccount);
  }
}
