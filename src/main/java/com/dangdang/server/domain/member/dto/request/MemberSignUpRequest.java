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

  public MemberSignUpRequest(String townName, String nickname, String phoneNumber,
      String profileImgUrl) {
    this.townName = townName;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
  }

  public static Member toMember(MemberSignUpRequest memberSignUpDto) {
    return new Member(memberSignUpDto.getNickname(),
        memberSignUpDto.getPhoneNumber(),
        memberSignUpDto.getProfileImgUrl());
  }
}
