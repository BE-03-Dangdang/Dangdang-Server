package com.dangdang.server.domain.account.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Account extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "account_id")
  private Long id;

  protected Account() {
  }

}
