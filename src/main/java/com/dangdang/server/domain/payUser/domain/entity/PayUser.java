package com.dangdang.server.domain.payUser.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class PayUser extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "pay_user_id")
  private Long id;

  protected PayUser() {
  }

}
