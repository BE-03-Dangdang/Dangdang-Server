package com.dangdang.server.domain.member.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.dto.response.MemberSignUpResponse;
import java.util.Collection;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
public class Member extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @Column(nullable = false, length = 30)
  private String nickname;

  @Column(nullable = false, unique = true, length = 30)
  private String phoneNumber;

  @Column(unique = true)
  @Lob
  private String profileImgUrl;

  protected Member() {
  }

  public Member(Long id, String nickname, String phoneNumber, String profileImgUrl) {
    this.id = id;
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
  }

  public Member(String nickname, String phoneNumber, String profileImgUrl) {
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
  }

  public Member(String nickname, String phoneNumber) {
    this.nickname = nickname;
    this.phoneNumber = phoneNumber;
  }

  public static MemberSignUpResponse from(Member member) {
    return new MemberSignUpResponse(member.getPhoneNumber());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return String.valueOf(this.id);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
