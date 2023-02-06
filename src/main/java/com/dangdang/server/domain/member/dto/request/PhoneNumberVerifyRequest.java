package com.dangdang.server.domain.member.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record PhoneNumberVerifyRequest(
    @NotBlank(message = "핸드폰 번호는 필수 입니다.")
    @Pattern(regexp = "[\\d]{11}", message = "핸드폰 번호 또는 인증코드가 잘못되었습니다.")
    String phoneNumber
) {

}
