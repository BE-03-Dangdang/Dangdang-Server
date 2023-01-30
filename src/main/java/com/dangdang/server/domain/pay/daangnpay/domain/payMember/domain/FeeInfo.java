package com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain;

import lombok.Getter;

@Getter
public class FeeInfo {

  private final int feeAmount;
  private final int freeMonthlyFeeCount;

  public FeeInfo(int feeAmount, int freeFeeCount) {
    this.feeAmount = feeAmount;
    this.freeMonthlyFeeCount = freeFeeCount;
  }
}
