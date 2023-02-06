package com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto;

import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingResponse;
import java.time.LocalDateTime;

public record PayResponse(String bank, String accountNumber, Integer money,
                          LocalDateTime createdAt) {

  public static PayResponse from(OpenBankingResponse openBankingResponse, int money) {
    return new PayResponse(openBankingResponse.bankName(), openBankingResponse.accountNumber(),
        money,
        openBankingResponse.createdAt());
  }
}
