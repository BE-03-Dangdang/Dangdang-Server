package com.dangdang.server.domain.town.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Cleanup;
import lombok.Getter;

@Entity
@Getter
public class Town extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "town_id")
  private Long id;

  @Column(length = 30)
  private String name;

  @Column(precision = 18, scale = 10)
  private BigDecimal longitude;

  @Column(precision = 18, scale = 10)
  private BigDecimal latitude;

  protected Town() {
  }

  public Town(String name, BigDecimal longitude, BigDecimal latitude) {
    this.name = name;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
