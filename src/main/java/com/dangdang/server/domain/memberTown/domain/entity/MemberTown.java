package com.dangdang.server.domain.memberTown.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.town.domain.entity.Town;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class MemberTown extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "member_town_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "town_id")
  private Town town;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private RangeType rangeType;

  protected MemberTown() {
  }


}
