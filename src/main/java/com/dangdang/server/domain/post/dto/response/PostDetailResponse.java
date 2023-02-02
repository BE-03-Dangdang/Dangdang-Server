package com.dangdang.server.domain.post.dto.response;


import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public record PostDetailResponse(@JsonProperty("postResponse") PostResponse postResponse,
                                 @JsonProperty("member") Member member,
                                 @JsonProperty("imageUrls") List<String> imageUrls) {

  public static PostDetailResponse from(Post post, Member member, List<String> imageUrls) {
    return new PostDetailResponse(PostResponse.from(post), member, imageUrls);
  }

  public Long getPostId() {
    return postResponse.getId();
  }

}
