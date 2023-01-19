package com.dangdang.server.domain.post.dto.response;

import com.dangdang.server.domain.post.domain.entity.Post;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostSliceResponse {

  private Long id;
  private String title;
  private String townName;
  private LocalDateTime createdAt;

  private PostSliceResponse() {
  }

  private PostSliceResponse(Long id, String title, String townName, LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.townName = townName;
    this.createdAt = createdAt;
  }

  public static PostSliceResponse from(Post post) {
    return new PostSliceResponse(post.getId(), post.getTitle(), post.getTown().getName(),
        post.getCreatedAt());
  }
}
