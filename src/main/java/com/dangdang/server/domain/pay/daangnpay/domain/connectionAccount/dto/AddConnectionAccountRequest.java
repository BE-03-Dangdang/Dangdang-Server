package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto;

import javax.validation.constraints.NotNull;

public record AddConnectionAccountRequest(@NotNull Long bankAccountId) {

}
