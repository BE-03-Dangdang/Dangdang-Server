package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import com.dangdang.server.domain.pay.kftc.feignClient.domain.OpenBankingMember;

public record GetAuthTokenRequest(
    String code,
    String state) {

  public static OpenBankingMember to(GetAuthTokenRequest getAuthTokenRequest) {
    return new OpenBankingMember(getAuthTokenRequest.code);
  }
}
