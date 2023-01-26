package com.dangdang.server.domain.member.dto.response;

import lombok.Getter;

@Getter
public class TestSendMessageResponse {

  private String authCode;

  public TestSendMessageResponse(String authCode) {
    this.authCode = authCode;
  }
}
