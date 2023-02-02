package com.dangdang.server.domain.review.application;

import static com.dangdang.server.global.exception.ExceptionCode.MEMBER_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.REVIEW_WRONG_ACCESS;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.review.domain.ReviewRepository;
import com.dangdang.server.domain.review.domain.entity.Review;
import com.dangdang.server.domain.review.dto.ReviewRequest;
import com.dangdang.server.domain.review.dto.ReviewResponse;
import com.dangdang.server.domain.review.exception.ReviewWrongAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final PostRepository postRepository;
  private final MemberRepository memberRepository;

  public ReviewService(ReviewRepository reviewRepository, PostRepository postRepository,
      MemberRepository memberRepository) {
    this.reviewRepository = reviewRepository;
    this.postRepository = postRepository;
    this.memberRepository = memberRepository;
  }

  @Transactional
  public ReviewResponse saveReview(ReviewRequest reviewRequest, Member reviewer) {
    Post foundPost = postRepository.findById(reviewRequest.postId())
        .orElseThrow(() -> new PostNotFoundException(
            POST_NOT_FOUND));

    canWriteReview(foundPost);

    Member reviewee = memberRepository.findById(reviewRequest.revieweeId())
        .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

    Review review = reviewRepository.save(
        ReviewRequest.to(reviewRequest, foundPost, reviewer, reviewee));
    return ReviewResponse.from(review);
  }

  private void canWriteReview(Post post) {
    if (post.getStatus() != StatusType.COMPLETED) {
      throw new ReviewWrongAccessException(REVIEW_WRONG_ACCESS);
    }
  }
}
