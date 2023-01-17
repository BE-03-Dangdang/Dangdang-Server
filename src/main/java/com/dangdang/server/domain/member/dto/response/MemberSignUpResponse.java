package com.dangdang.server.domain.member.dto.response;

public class MemberSignUpResponse {
  private String phoneNumber;

  private MemberSignUpResponse() {
  }

  public MemberSignUpResponse(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
