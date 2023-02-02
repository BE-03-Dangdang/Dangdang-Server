package com.dangdang.server.domain.pay.banks.bankAccount.domain.entity;

import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_AUTHENTICATION_FAIL;
import static com.dangdang.server.global.exception.ExceptionCode.BANK_ACCOUNT_INACTIVE;
import static com.dangdang.server.global.exception.ExceptionCode.INSUFFICIENT_BALANCE;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.BankAccountAuthenticationException;
import com.dangdang.server.domain.pay.banks.bankAccount.exception.InactiveBankAccountException;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.InsufficientBankAccountException;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
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

  @Column(length = 100, nullable = false)
  private String accountNumber;

  @Column(length = 20, nullable = false)
  private String bankName;

  @Column(length = 10, nullable = false)
  private String clientName;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("0")
  private Integer balance = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pay_member_id")
  private PayMember payMember;

  protected BankAccount() {
  }

  public BankAccount(String accountNumber, String bankName, Integer balance, PayMember payMember,
      String clientName) {
    this.accountNumber = accountNumber;
    this.bankName = bankName;
    this.balance = balance;
    this.payMember = payMember;
    this.clientName = clientName;
  }

  public BankAccount(String accountNumber, String bankName, Integer balance, PayMember payMember,
      String clientName, StatusType statusType) {
    this.accountNumber = accountNumber;
    this.bankName = bankName;
    this.balance = balance;
    this.payMember = payMember;
    this.status = statusType;
    this.clientName = clientName;
  }

  private void verifyStatus() {
    if (this.status == StatusType.INACTIVE) {
      throw new InactiveBankAccountException(BANK_ACCOUNT_INACTIVE);
    }
  }

  private void verifyPayMemberId(long requestPayMemberId) {
    Long payMemberId = this.payMember.getId();
    if (!payMemberId.equals(requestPayMemberId)) {
      throw new BankAccountAuthenticationException(BANK_ACCOUNT_AUTHENTICATION_FAIL);
    }
  }

  public void withdraw(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    verifyStatus();
    verifyPayMemberId(openBankingWithdrawRequest.payMemberId());
    Integer requestAmount = openBankingWithdrawRequest.amount();
    if (balance < requestAmount) {
      throw new InsufficientBankAccountException(INSUFFICIENT_BALANCE);
    }
    balance -= requestAmount;
  }

  public void deposit(OpenBankingDepositRequest openBankingDepositRequest) {
    verifyStatus();
    verifyPayMemberId(openBankingDepositRequest.payMemberId());
    balance += openBankingDepositRequest.amount();
  }
}
