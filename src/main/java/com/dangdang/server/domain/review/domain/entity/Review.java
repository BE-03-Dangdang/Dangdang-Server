package com.dangdang.server.domain.review.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Review extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "review_id")
  private Long id;

  protected Review() {
  }

}
