package com.dangdang.server.domain.remittance.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Remittance extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "remittance_id")
  private Long id;

  protected Remittance() {
  }

}
