package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.RedisSms;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SmsRequest {

  @NotBlank
  @Pattern(regexp = "[\\d]{11}", message = "핸드폰 번호 또는 인증코드가 잘못되었습니다.")
  private String toPhoneNumber;

  private SmsRequest() {
  }

  public SmsRequest(String toPhoneNumber) {
    this.toPhoneNumber = toPhoneNumber;
  }

  public static RedisSms toRedisSms(SmsRequest smsRequest, String authCode) {
    return new RedisSms(smsRequest.toPhoneNumber, authCode);
  }
}
