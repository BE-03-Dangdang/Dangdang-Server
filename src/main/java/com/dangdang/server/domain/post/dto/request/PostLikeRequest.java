package com.dangdang.server.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class PostLikeRequest {

  private Long postId;
  private Long memberId;

  @JsonCreator
  public PostLikeRequest(Long postId, Long memberId) {
    this.postId = postId;
    this.memberId = memberId;
  }
}
