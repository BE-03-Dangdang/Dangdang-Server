package com.dangdang.server.domain.post.dto.response;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.global.util.S3ImageUtil;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostSliceResponse {

  private Long id;
  private String title;
  private String townName;
  private String imageUrl;
  private LocalDateTime createdAt;
  private int likeCount;

  private PostSliceResponse(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.townName = post.getTownName();
    this.imageUrl = S3ImageUtil.makeImageLink(post.getImageUrl());
    this.createdAt = post.getCreatedAt();
    this.likeCount = post.getLikeCount();
  }

  public static PostSliceResponse from(Post post) {
    return new PostSliceResponse(post);
  }
}
