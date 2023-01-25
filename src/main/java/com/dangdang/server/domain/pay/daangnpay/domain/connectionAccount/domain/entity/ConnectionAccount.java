package com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
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

  @Column(length = 20)
  private String bank;

  @Column(length = 100)
  private String bankAccountNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected ConnectionAccount() {
  }

  public ConnectionAccount(BankAccount bankAccount, PayMember payMember) {
    this.bank = bankAccount.getBankName();
    this.bankAccountNumber = bankAccount.getAccountNumber();
    this.payMember = payMember;
  }
}
