package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetConnectionAccountReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.FeeInfo;

public record RemittanceResponse(
    String chargeAccountBankName,
    String chargeAccountNumber,
    int autoChargeAmount,
    int feeAmount,
    int freeMonthlyFeeCount,
    int balanceMoney) {

  public static RemittanceResponse of(
      GetConnectionAccountReceiveResponse getConnectionAccountReceiveResponse, int autoChargeAmount,
      FeeInfo feeInfo, PayResponse payResponse) {
    return new RemittanceResponse(getConnectionAccountReceiveResponse.chargeAccountBankName(),
        getConnectionAccountReceiveResponse.chargeAccountNumber(), autoChargeAmount,
        feeInfo.getFeeAmount(), feeInfo.getFreeMonthlyFeeCount(), payResponse.money());
  }
}
