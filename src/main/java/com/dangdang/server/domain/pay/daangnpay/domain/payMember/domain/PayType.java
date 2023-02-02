package com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain;

public enum PayType {

  WITHDRAW(1, "출금"), CHARGE(10000, "충전"), REMITTANCE(1, "송금");

  final int minAmount;

  final String usageDetail;

  PayType(int minAmount, String usageDetail) {
    this.minAmount = minAmount;
    this.usageDetail = usageDetail;
  }

  public String getUsageDetail() {
    return usageDetail;
  }

  public boolean checkMinAmount(int amountRequest) {
    return amountRequest >= this.minAmount;
  }
}
