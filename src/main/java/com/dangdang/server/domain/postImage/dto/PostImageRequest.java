package com.dangdang.server.domain.postImage.dto;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostImageRequest(
    @JsonProperty List<String> urls
) {

  private static final int REPRESENTATIVE_IMAGE_INDEX = 0;

  public static PostImage toPostImage(Post post, String url) {
    return new PostImage(post, url);
  }

  public String representativeImage() {
    return urls.get(REPRESENTATIVE_IMAGE_INDEX);
  }

}
