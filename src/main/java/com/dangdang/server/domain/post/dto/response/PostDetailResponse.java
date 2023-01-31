package com.dangdang.server.domain.post.dto.response;


import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import java.util.List;
import lombok.Getter;

@Getter
public class PostDetailResponse {

  PostResponse postResponse;
  Member member;
  List<String> imageUrls;

  private PostDetailResponse(PostResponse postResponse, Member member, List<String> imageUrls) {
    this.postResponse = postResponse;
    this.member = member;
    this.imageUrls = imageUrls;
  }

  public static PostDetailResponse from(Post post, Member member, List<String> imageUrls) {
    return new PostDetailResponse(PostResponse.from(post), member, imageUrls);
  }

  public Long getPostId() {
    return postResponse.getId();
  }

}
