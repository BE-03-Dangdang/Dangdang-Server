package com.dangdang.server.domain.connectionAccount.domain.entity;

import com.dangdang.server.domain.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.payMember.domain.entity.PayMember;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class ConnectionAccount extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "connection_account_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_account_id")
  private BankAccount bankAccount;

  protected ConnectionAccount() {
  }

  public ConnectionAccount(PayMember payMember, BankAccount bankAccount) {
    this.payMember = payMember;
    this.bankAccount = bankAccount;
  }
}
