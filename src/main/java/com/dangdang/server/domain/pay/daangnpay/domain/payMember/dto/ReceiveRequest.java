package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReceiveRequest(
    String openBankingToken,
    @NotNull @Min(1)
    int depositAmount,
    @NotBlank
    String bankAccountNumber,
    @NotBlank
    String bankName
) {

}
