package com.dangdang.server.domain.post.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Post extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "post_id")
  private Long id;

  protected Post() {
  }

}
