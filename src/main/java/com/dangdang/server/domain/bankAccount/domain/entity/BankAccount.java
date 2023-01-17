package com.dangdang.server.domain.bankAccount.domain.entity;

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

@Entity
@Getter
public class BankAccount extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "bank_account_id")
  private Long id;

  @Column(length = 100)
  private String accountNumber;

  @Column(length = 20)
  private String bank;

  @Column(columnDefinition = "INT UNSIGNED")
  private Integer balance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected BankAccount() {
  }

}
