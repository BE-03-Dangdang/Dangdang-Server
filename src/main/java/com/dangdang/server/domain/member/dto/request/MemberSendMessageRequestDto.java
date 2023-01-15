package com.dangdang.server.domain.member.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MemberSendMessageRequestDto {

  @NotBlank(message = "핸드폰 번호는 필수 입니다.")
  @Pattern(regexp = "[\\d]{11}")
  private String toNumber;

  protected MemberSendMessageRequestDto() {

  }
}
