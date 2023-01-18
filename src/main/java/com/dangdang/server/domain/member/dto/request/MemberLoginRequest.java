package com.dangdang.server.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberLoginRequest {

  private String phoneNumber;

  private MemberLoginRequest() {

  }

}
