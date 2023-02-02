package com.dangdang.server.domain.pay.banks.trustAccount.domain.entity;

import static com.dangdang.server.global.exception.ExceptionCode.INSUFFICIENT_BALANCE;
import static com.dangdang.server.global.exception.ExceptionCode.TRUST_ACCOUNT_INACTIVE;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.pay.banks.trustAccount.exception.InactiveTrustAccountException;
import com.dangdang.server.domain.pay.banks.trustAccount.exception.InsufficientTrustAccountException;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingDepositRequest;
import com.dangdang.server.domain.pay.kftc.common.dto.OpenBankingWithdrawRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class TrustAccount extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trust_account_id")
  private Long id;

  @Column(length = 100, nullable = false)
  private String accountNumber;

  @Column(columnDefinition = "INT UNSIGNED", nullable = false)
  @ColumnDefault("0")
  private Integer balance;

  @Column(length = 100, nullable = false)
  private String customer;

  protected TrustAccount() {
  }

  public TrustAccount(String accountNumber, Integer balance, String customer) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.customer = customer;
  }

  public TrustAccount(String accountNumber, Integer balance, String customer,
      StatusType statusType) {
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.customer = customer;
    this.status = statusType;
  }

  private void verifyTrustAccountStatus() {
    if (this.status == StatusType.INACTIVE) {
      throw new InactiveTrustAccountException(TRUST_ACCOUNT_INACTIVE);
    }
  }

  public void deposit(OpenBankingWithdrawRequest openBankingWithdrawRequest) {
    verifyTrustAccountStatus();
    balance += openBankingWithdrawRequest.amount();
  }

  public void withdraw(OpenBankingDepositRequest openBankingDepositRequest) {
    verifyTrustAccountStatus();
    if (balance < openBankingDepositRequest.amount()) {
      throw new InsufficientTrustAccountException(INSUFFICIENT_BALANCE);
    }

    balance -= openBankingDepositRequest.amount();
  }
}
