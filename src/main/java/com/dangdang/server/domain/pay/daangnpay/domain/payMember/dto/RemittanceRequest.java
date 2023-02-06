package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record RemittanceRequest(
    String openBankingToken,
    @NotNull @Min(1)
    int depositAmount,
    @NotBlank
    String receiveClientName,
    @NotBlank
    String bankAccountNumber,
    @NotBlank
    String bankName
) {

}
