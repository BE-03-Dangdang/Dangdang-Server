package com.dangdang.server.domain.member.dto.response;

import lombok.Getter;

@Getter
public class MemberCertifyResponse {

  private String accessToken;
  private Boolean isCertified;

  private MemberCertifyResponse() {

  }

  public MemberCertifyResponse(String accessToken, Boolean isCertified) {
    this.accessToken = accessToken;
    this.isCertified = isCertified;
  }
}
