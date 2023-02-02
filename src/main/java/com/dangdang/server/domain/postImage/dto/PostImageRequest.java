package com.dangdang.server.domain.postImage.dto;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostImageRequest(
    @JsonProperty List<String> urls
) {

  public static PostImage toPostImage(Post post, String url) {
    return new PostImage(post, url);
  }
}
