package com.dangdang.server.domain.member.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.dto.response.MemberSignUpResponse;
import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
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


  @Column(nullable = false, unique = true, length = 30)
  private String phoneNumber;

  @Column
  @Lob
  private String profileImgUrl;

  @Column(nullable = false, length = 30)
  private String nickname;
  
  @Column(nullable = true)
  private String refreshToken;
  protected Member() {

  }

  public Member(Long id, String phoneNumber, String nickname) {
    this.id = id;
    this.phoneNumber = phoneNumber;
    this.nickname = nickname;
  }

  public Member(String phoneNumber, String profileImgUrl, String nickname) {
    this.phoneNumber = phoneNumber;
    this.profileImgUrl = profileImgUrl;
    this.nickname = nickname;
  }

  public Member(String phoneNumber, String nickname) {
    this.phoneNumber = phoneNumber;
    this.nickname = nickname;
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
  
  public void isId(Long memberId) {
    if (!Objects.equals(this.id, memberId)) {
      throw new BusinessException(ExceptionCode.NOT_PERMISSION);
    }
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
