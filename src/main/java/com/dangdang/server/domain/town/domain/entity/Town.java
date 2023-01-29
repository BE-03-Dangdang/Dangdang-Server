package com.dangdang.server.domain.town.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;

@Getter
@Entity
public class Town extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "town_id")
  private Long id;

  @Column(length = 30, unique = true)
  private String name;

  @Column(precision = 18, scale = 10)
  private BigDecimal latitude;

  @Column(precision = 18, scale = 10)
  private BigDecimal longitude;

  @OneToMany(mappedBy = "town")
  private List<MemberTown> memberTownList;

  protected Town() {
  }

  public Town(String name, BigDecimal latitude, BigDecimal longitude) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
