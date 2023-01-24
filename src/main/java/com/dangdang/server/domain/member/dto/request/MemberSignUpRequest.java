package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.Member;
import lombok.Getter;

@Getter
public class MemberSignUpRequest {

  private final String townName;
  private final String phoneNumber;
  private final String profileImgUrl;
  private final String nickname;


  public MemberSignUpRequest(String townName, String phoneNumber, String profileImgUrl,
      String nickname) {
    this.townName = townName;
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
    this.nickname = nickname;
  }

  public static Member toMember(MemberSignUpRequest memberSignUpDto) {
    return new Member(memberSignUpDto.getPhoneNumber(),
        memberSignUpDto.getProfileImgUrl(),
        memberSignUpDto.getNickname());
  }

}
