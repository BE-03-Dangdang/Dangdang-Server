package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PayRequest(
    String openBankingToken,
    @NotBlank String bankName,
    @NotBlank String bankAccountNumber,
    @NotNull Integer amount) {

}
