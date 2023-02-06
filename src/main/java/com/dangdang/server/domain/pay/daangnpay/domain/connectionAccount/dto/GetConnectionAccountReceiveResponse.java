package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

public record GetConnectionAccountReceiveResponse(
    boolean isMyAccount,
    String chargeAccountBankName,
    String chargeAccountNumber
) {

}
