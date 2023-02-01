package com.dangdang.server.domain.member.dto.response;

import com.dangdang.server.domain.member.domain.entity.Member;

public record MemberResponse(
    Long id,
    String profileImgUrl,
    String nickName
) {

  public static MemberResponse from(Member member) {
    return new MemberResponse(member.getId(), member.getProfileImgUrl(), member.getNickname());
  }

}