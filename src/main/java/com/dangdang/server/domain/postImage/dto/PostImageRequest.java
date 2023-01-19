package com.dangdang.server.domain.postImage.dto;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import lombok.Getter;

@Getter
public class PostImageRequest {

  private List<String> url;

  @JsonCreator
  public PostImageRequest(List<String> url) {
    this.url = url;
  }

  public static PostImage toPostImage(Post post, String url) {
    return new PostImage(post, url);
  }
}
