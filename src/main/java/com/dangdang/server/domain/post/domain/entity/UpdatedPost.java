package com.dangdang.server.domain.post.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.post.domain.Category;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class UpdatedPost extends BaseEntity {

  @Id
  @Column(name = "post_id")
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, length = 1000)
  private String content;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  @Column(columnDefinition = "INT UNSIGNED")
  private Integer price;

  @Column(nullable = false)
  @ColumnDefault("0")
  private Integer view = 0;

  @Column(nullable = false)
  @ColumnDefault("false")
  private Boolean sharing = false;

  private Long townId;

  @Column(length = 30)
  private String townName;

  @Lob
  private String imageUrl;

  protected UpdatedPost() {
  }

  private UpdatedPost(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.category = post.getCategory();
    this.price = post.getPrice();
    this.sharing = post.getSharing();
    this.townId = post.getTown().getId();
    this.townName = post.getTownName();
    this.imageUrl = post.getImageUrl();
    super.createdAt = post.getCreatedAt();
    super.status = post.getStatus();
  }

  public static UpdatedPost from(Post post) {
    return new UpdatedPost(post);
  }
}
