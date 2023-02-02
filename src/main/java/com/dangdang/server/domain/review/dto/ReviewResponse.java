package com.dangdang.server.domain.review.dto;

import com.dangdang.server.domain.member.dto.response.MemberResponse;
import com.dangdang.server.domain.review.domain.entity.Review;

public record ReviewResponse(Long reviewId, String postTitle, String townName,
                             MemberResponse reviewer,
                             MemberResponse reviewee,
                             String preference,
                             String nicePoint,
                             String content) {

  public static ReviewResponse from(Review review) {
    return new ReviewResponse(review.getId(), review.getPost().getTitle(),
        review.getPost().getTownName(), MemberResponse.from(review.getReviewer()),
        MemberResponse.from(review.getReviewee()), review.getPreference(),
        review.getNicePoint(), review.getContent());
  }

}
