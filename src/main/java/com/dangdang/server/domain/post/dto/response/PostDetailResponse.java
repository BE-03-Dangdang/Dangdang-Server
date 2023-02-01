package com.dangdang.server.domain.post.dto.response;


import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.dto.response.MemberResponse;
import com.dangdang.server.domain.post.domain.entity.Post;
import java.util.List;
import lombok.Getter;

@Getter
public class PostDetailResponse {

  PostResponse postResponse;
  MemberResponse memberResponse;
  List<String> imageUrls;

  private PostDetailResponse(PostResponse postResponse, MemberResponse memberResponse,
      List<String> imageUrls) {
    this.postResponse = postResponse;
    this.memberResponse = memberResponse;
    this.imageUrls = imageUrls;
  }

  public static PostDetailResponse from(Post post, Member member, List<String> imageUrls) {
    return new PostDetailResponse(PostResponse.from(post), MemberResponse.from(member), imageUrls);
  }

  public Long postId() {
    return postResponse.getId();
  }

}
