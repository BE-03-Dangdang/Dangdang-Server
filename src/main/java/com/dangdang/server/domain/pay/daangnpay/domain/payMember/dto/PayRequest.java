package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record PayRequest(
    @NotNull Long bankAccountId,
    @NotNull @Min(10000) Integer amount) {

}
