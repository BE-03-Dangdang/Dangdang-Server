package com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.FeeInfo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class PayMember extends BaseEntity {

  private static final int BASIC_FEE_AMOUNT = 500;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pay_user_id")
  private Long id;

  @Column(length = 200, nullable = false)
  private String password;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("0")
  private Integer money = 0;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("5")
  private Integer freeMonthlyFeeCount = 5;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  protected PayMember() {
  }

  public PayMember(String password, Member member) {
    this.password = password;
    this.member = member;
  }

  public PayMember(String password, Integer money, Member member) {
    this.password = password;
    this.money = money;
    this.member = member;
  }

  public PayMember(Integer money, Integer freeMonthlyFeeCount) {
    this.money = money;
    this.freeMonthlyFeeCount = freeMonthlyFeeCount;
  }

  public int addMoney(int amount) {
    money += amount;
    return money;
  }

  public int minusMoney(int amount) {
    money -= amount;
    return money;
  }

  public int calculateAutoChargeAmount(int depositAmountRequest) {
    if (depositAmountRequest <= money) {
      return 0;
    }

    final int minChargeAmount = 1000;
    int expectChargeAmount = depositAmountRequest - money;
    return Math.max(expectChargeAmount, minChargeAmount);
  }

  public FeeInfo getFeeInfo() {
    if (freeMonthlyFeeCount == 0) {
      return new FeeInfo(BASIC_FEE_AMOUNT, freeMonthlyFeeCount);
    }
    return new FeeInfo(0, freeMonthlyFeeCount - 1);
  }

  public void changeFeeCount() {
    if (freeMonthlyFeeCount > 0) {
      freeMonthlyFeeCount -= 1;
    }
  }
}
