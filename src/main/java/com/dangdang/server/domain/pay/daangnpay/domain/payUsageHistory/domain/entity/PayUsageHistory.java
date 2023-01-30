package com.dangdang.server.domain.pay.daangnpay.domain.payUsageHistory.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.PayType;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
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
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class PayUsageHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pay_usage_history_id")
  private Long id;

  @Column(length = 255, nullable = false)
  private String title;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("0")
  private Integer amount = 0;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  private Integer balanceMoney;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("0")
  private Integer fee = 0;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private PayType payType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected PayUsageHistory() {
  }

  public PayUsageHistory(String title, Integer balanceMoney, PayMember payMember, PayType payType) {
    this.title = title;
    this.balanceMoney = balanceMoney;
    this.payMember = payMember;
    this.payType = payType;
  }
}
