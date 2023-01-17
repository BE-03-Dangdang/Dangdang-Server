package com.dangdang.server.domain.payUsageHistory.domain.entity;

import com.dangdang.server.domain.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.payMember.domain.entity.PayMember;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class PayUsageHistory extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "pay_usage_history_id")
  private Long id;

  @Column(length = 255)
  private String title;

  @Column(columnDefinition = "INT UNSIGNED")
  @ColumnDefault("0")
  private Integer amount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_account_id")
  private BankAccount bankAccount;

  protected PayUsageHistory() {
  }
}
