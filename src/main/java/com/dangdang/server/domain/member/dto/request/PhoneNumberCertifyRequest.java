package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public record PhoneNumberCertifyRequest (
    @NotBlank(message = "핸드폰 번호는 필수 입니다.")
    @Pattern(regexp = "[\\d]{11}", message = "핸드폰 번호 또는 인증코드가 잘못되었습니다.")
    String phoneNumber,
    @NotBlank(message = "인증코드는 필수 입니다.")
    @Pattern(regexp = "[\\d]{6}", message = "핸드폰 번호 또는 인증코드가 잘못되었습니다.")
    String authCode
) {

  public static RedisAuthCode toRedisAuthCode(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    return new RedisAuthCode(phoneNumberCertifyRequest.phoneNumber);
  }
}
