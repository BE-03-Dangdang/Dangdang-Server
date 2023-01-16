package com.dangdang.server.domain.member.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;

@Entity
@Getter
public class Member extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  @Column(nullable = false, length = 30)
  private String nickname;

  @Column(nullable = false, length = 30)
  private String phoneNumber;

  @Column
  @Lob
  private String profileImgUrl;

  protected Member() {
  }
}
