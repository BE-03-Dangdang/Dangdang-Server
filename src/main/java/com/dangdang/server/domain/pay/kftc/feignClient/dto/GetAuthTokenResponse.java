package com.dangdang.server.domain.pay.kftc.feignClient.dto;

import com.dangdang.server.domain.pay.kftc.feignClient.domain.OpenBankingMember;

public record GetAuthTokenResponse(
    String access_token,
    String token_type,
    String refresh_token,
    String expires_in,
    String scope,
    String user_seq_no) {

  public static OpenBankingMember to(GetAuthTokenResponse getAuthTokenResponse) {
    return new OpenBankingMember(getAuthTokenResponse.access_token(),
        getAuthTokenResponse.refresh_token(), getAuthTokenResponse.user_seq_no());
  }
}
