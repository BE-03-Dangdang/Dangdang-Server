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

  private PostSliceResponse(Long id, String title, String townName, String imageUrl,
      LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.townName = townName;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
  }

  public static PostSliceResponse from(Post post) {
    return new PostSliceResponse(post.getId(), post.getTitle(), post.getTown().getName(),
        S3ImageUtil.makeImageLink(post.getImageUrl()), post.getCreatedAt());
  }
}
