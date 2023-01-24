package com.dangdang.server.domain.memberTown.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.town.domain.entity.Town;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Getter;

@Entity
@Getter
public class MemberTown extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_town_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "town_id")
  private Town town;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private RangeType rangeType;

  protected MemberTown() {
  }

  public MemberTown(Member member, Town town) {
    this.member = member;
    this.town = town;
    this.rangeType = RangeType.LEVEL2;
    this.status = StatusType.ACTIVE;
  }

  public MemberTown(Long id, Member member, Town town, RangeType rangeType) {
    this.id = id;
    this.member = member;
    this.town = town;
    this.rangeType = rangeType;
  }

  public MemberTown(StatusType statusType) {
    this.status = statusType;
  }

  public void isOwner(Long memberId) {
    this.member.isId(memberId);
  }

  public void update(MemberTown updateMemberTown) {
    this.status = updateMemberTown.getStatus();
  }

}
