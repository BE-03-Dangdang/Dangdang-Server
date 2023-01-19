package com.dangdang.server.domain.trustAccount.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
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
  @Column(name = "bank_account_id")
  private Long id;

  @Column(length = 100)
  private String accountNumber;

  @Column(length = 20)
  private String bank;

  @Column(columnDefinition = "INT UNSIGNED")
  @ColumnDefault("0")
  private Integer balance;

  protected TrustAccount() {
  }

}
