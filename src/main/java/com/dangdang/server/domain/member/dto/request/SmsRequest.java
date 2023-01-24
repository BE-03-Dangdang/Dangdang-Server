package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.RedisSms;
import lombok.Getter;

@Getter
public class SmsRequest {

  private String toPhoneNumber;

  private SmsRequest() {
  }

  public static RedisSms toRedisSms(SmsRequest smsRequest, String authCode) {
    return new RedisSms(smsRequest.toPhoneNumber, authCode);
  }
}
