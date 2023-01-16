package com.dangdang.server.domain.postImage.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.post.domain.entity.Post;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class PostImage extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "post_image_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Lob
  @Column(nullable = false)
  private String url;

  protected PostImage() {
  }

}
