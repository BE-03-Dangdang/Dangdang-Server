package com.dangdang.server.domain.member.dto.response;

public class MemberLoginResponse {

  private String ACCESS_TOKEN;

  private MemberLoginResponse() {

  }

  public MemberLoginResponse(String ACCESS_TOKEN) {
    this.ACCESS_TOKEN = ACCESS_TOKEN;
  }
}
