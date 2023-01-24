package com.dangdang.server.domain.member.dto.request;

import com.dangdang.server.domain.member.domain.entity.Member;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemberSignUpRequest {

  @NotBlank
  @Pattern(regexp = "[가-힣]+")
  private String townName;
  @NotBlank
  @Pattern(regexp = "[\\d]{11}", message = "핸드폰 번호 또는 인증코드가 잘못되었습니다.")
  private String phoneNumber;
  private String profileImgUrl;
  @Length(max=12, min=2)
  @Pattern(regexp = "[가-힣|a-z|A-Z|0-9]+")
  private String nickname;

  private MemberSignUpRequest() {
  }

  public MemberSignUpRequest(String townName, String phoneNumber, String profileImgUrl,
      String nickname) {
    this.townName = townName;
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
    this.nickname = nickname;
  }

  public MemberSignUpRequest(String townName, String phoneNumber, String nickname) {
    this.townName = townName;
    this.phoneNumber = phoneNumber;
    this.nickname = nickname;
  }

  public static Member toMember(MemberSignUpRequest memberSignUpDto) {
    return new Member(memberSignUpDto.getPhoneNumber(),
        memberSignUpDto.getProfileImgUrl(),
        memberSignUpDto.getNickname());
  }

}
