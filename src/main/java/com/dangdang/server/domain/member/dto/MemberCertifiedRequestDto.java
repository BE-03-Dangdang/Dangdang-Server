package com.dangdang.server.domain.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberCertifiedRequestDto {

  @NotNull
  @NotBlank(message="핸드폰 번호는 필수 입니다.")
  @Pattern(regexp = "[0-9]{11}")
  private String toNumber;
  private String randomNumber;

  protected MemberCertifiedRequestDto() {
  }
}
