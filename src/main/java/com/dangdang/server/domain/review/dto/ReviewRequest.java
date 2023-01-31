package com.dangdang.server.domain.review.dto;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.review.domain.entity.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReviewRequest(
    @NotNull @JsonProperty("postId") Long postId,
    @NotNull @JsonProperty("reviewerId") Long reviewerId,
    @NotNull @JsonProperty("revieweeId") Long revieweeId,
    @NotBlank @JsonProperty("preference") String preference,
    @NotBlank @JsonProperty("nicePoint") String nicePoint,
    @NotBlank @JsonProperty("content") String content
) {

  public static Review to(ReviewRequest reviewRequest, Post post, Member reviewer,
      Member reviewee) {
    return new Review(post, reviewer, reviewee,
        reviewRequest.preference, reviewRequest.nicePoint, reviewRequest.content);
  }

}
