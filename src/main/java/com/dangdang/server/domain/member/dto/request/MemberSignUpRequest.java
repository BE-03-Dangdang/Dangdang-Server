package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.Member;
import lombok.Getter;

@Getter
public class MemberSignUpRequest {

  private String nickname;
  private String phoneNumber;
  private String profileImgUrl;
  private String townName;

  private MemberSignUpRequest() {

  }

  public static Member toMember(MemberSignUpRequest memberSignUpDto) {
    return new Member(memberSignUpDto.nickname,
        memberSignUpDto.phoneNumber,
        memberSignUpDto.profileImgUrl);
  }
}
