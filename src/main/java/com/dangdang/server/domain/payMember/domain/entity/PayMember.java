package com.dangdang.server.domain.payMember.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class PayMember extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "pay_user_id")
  private Long id;

  @Column(length = 200)
  private String password;

  @Column(columnDefinition = "INT UNSIGNED")
  @ColumnDefault("0")
  private Integer money = 0;

  @Column(columnDefinition = "INT UNSIGNED")
  @ColumnDefault("5")
  private Integer fee_count = 5;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  protected PayMember() {
  }

}