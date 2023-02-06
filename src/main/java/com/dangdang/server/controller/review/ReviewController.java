package com.dangdang.server.controller.review;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.review.application.ReviewService;
import com.dangdang.server.domain.review.dto.ReviewRequest;
import com.dangdang.server.domain.review.dto.ReviewResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @CurrentUserId
  @PostMapping("/reviews")
  public ResponseEntity<ReviewResponse> saveReview(@RequestBody ReviewRequest reviewRequest,
      Long memberId) {

    ReviewResponse reviewResponse = reviewService.saveReview(reviewRequest, memberId);
    return ResponseEntity.created(
            URI.create(
                "/" + reviewResponse.reviewer().id() + "/reviews-sent/"
                    + reviewResponse.reviewId()))
        .body(reviewResponse);
  }
}
