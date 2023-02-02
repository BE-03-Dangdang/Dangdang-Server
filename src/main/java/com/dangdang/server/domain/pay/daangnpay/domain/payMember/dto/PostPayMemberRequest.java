package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PostPayMemberRequest(@NotNull Long memberId, @NotBlank String state) {

}
