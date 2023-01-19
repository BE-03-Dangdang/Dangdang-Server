package com.dangdang.server.domain.bankAccount.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.common.StatusType;
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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class BankAccount extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bank_account_id")
  private Long id;

  @Column(length = 100)
  private String accountNumber;

  @Column(length = 20)
  private String bank;

  @Column(columnDefinition = "INT UNSIGNED")
  @ColumnDefault("0")
  private Integer balance = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected BankAccount() {
  }

  public BankAccount(String accountNumber, String bank, Integer balance, PayMember payMember) {
    this.accountNumber = accountNumber;
    this.bank = bank;
    this.balance = balance;
    this.payMember = payMember;
  }

  public BankAccount(String accountNumber, String bank, Integer balance, PayMember payMember,
      StatusType statusType) {
    this.accountNumber = accountNumber;
    this.bank = bank;
    this.balance = balance;
    this.payMember = payMember;
    this.status = statusType;
  }
}
