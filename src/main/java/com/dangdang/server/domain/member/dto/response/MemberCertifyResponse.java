package com.dangdang.server.domain.member.dto.response;

public record MemberCertifyResponse (
    String accessToken,
    String refreshToken,
    Boolean isCertified
) {

  public static MemberCertifyResponse from(String accessToken, String refreshToken ,Boolean isCertified) {
    return new MemberCertifyResponse(accessToken, refreshToken, isCertified);
  }
    Boolean isCertified
) {

}
