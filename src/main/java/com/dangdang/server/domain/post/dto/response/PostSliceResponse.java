package com.dangdang.server.domain.post.dto.response;

import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.domain.entity.PostSearch;
import com.dangdang.server.global.util.S3ImageUtil;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostSliceResponse {

  private final Long id;
  private final String title;
  private final String townName;
  private final String imageUrl;
  private final Integer price;
  private final LocalDateTime createdAt;
  private int likeCount;

  private PostSliceResponse(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.townName = post.getTownName();
    this.imageUrl = S3ImageUtil.makeImageLink(post.getImageUrl());
    this.createdAt = post.getCreatedAt();
    this.price = post.getPrice();
    this.likeCount = post.getLikeCount();
  }

  public static PostSliceResponse from(Post post) {
    return new PostSliceResponse(post.getId(), post.getTitle(), post.getTownName(),
        S3ImageUtil.makeImageLink(post.getImageUrl()), post.getPrice(), post.getCreatedAt());
  }

  public static PostSliceResponse from(PostSearch postSearch) {
    assert postSearch.getId() != null;
    return new PostSliceResponse(Long.parseLong(postSearch.getId()), postSearch.getTitle(),
        postSearch.getTownName(), postSearch.getImageUrl(), postSearch.getPrice(),
        postSearch.getCreatedAt());
  }
}
