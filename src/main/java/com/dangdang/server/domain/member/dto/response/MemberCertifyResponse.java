package com.dangdang.server.domain.member.dto.response;

import lombok.Getter;

@Getter
public record MemberCertifyResponse (
    String accessToken,
    Boolean isCertified
) {

  public static MemberCertifyResponse from(String accessToken, Boolean isCertified) {
    return new MemberCertifyResponse(accessToken, isCertified);
  }
}
