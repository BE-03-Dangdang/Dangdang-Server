package com.dangdang.server.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.dto.response.MemberResponse;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.review.domain.ReviewRepository;
import com.dangdang.server.domain.review.domain.entity.Review;
import com.dangdang.server.domain.review.dto.ReviewRequest;
import com.dangdang.server.domain.review.dto.ReviewResponse;
import com.dangdang.server.domain.review.exception.ReviewWrongAccessException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ReviewServiceTest {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private TownRepository townRepository;

  Post post;
  Member reviewer;
  Member reviewee;
  Town town;

  @BeforeEach
  void setup() {
    Member member1 = new Member("01044446780", null, "kw1");
    reviewer = memberRepository.save(member1);
    Member member2 = new Member("01011113456", null, "kw2");
    reviewee = memberRepository.save(member2);
    town = townRepository.findByName("천호동").get();

  }

  @Test
  @DisplayName("Review를 작성할 수 있다.")
  void saveReviewTest() {
    Post newPost = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
        new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, reviewer, town,
        null, StatusType.COMPLETED);

    post = postRepository.save(newPost);

    ReviewRequest reviewRequest = new ReviewRequest(post.getId(), reviewer.getId(),
        reviewee.getId(), "preference", "nicePoint", "content");
    ReviewResponse reviewResponse = reviewService.saveReview(reviewRequest);

    Review foundReview = reviewRepository.findById(reviewResponse.reviewId()).get();
    assertThat(foundReview.getId()).isEqualTo(reviewResponse.reviewId());
    assertThat(MemberResponse.from(foundReview.getReviewer())).usingRecursiveComparison()
        .isEqualTo(reviewResponse.reviewer());
    assertThat(MemberResponse.from(foundReview.getReviewee())).usingRecursiveComparison()
        .isEqualTo(reviewResponse.reviewee());
    assertThat(foundReview.getPost().getTitle()).isEqualTo(reviewResponse.postTitle());
  }

  @Test
  @DisplayName("완료된 거래가 아니라면 ReviewWrongAccess예외를 던진다.")
  void WrongAccess() {
    Post newPost = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
        new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, reviewer, town,
        null, StatusType.SELLING);

    post = postRepository.save(newPost);

    ReviewRequest reviewRequest = new ReviewRequest(post.getId(), reviewer.getId(),
        reviewee.getId(), "preference", "nicePoint", "content");

    assertThatThrownBy(() -> reviewService.saveReview(reviewRequest)).isInstanceOf(
        ReviewWrongAccessException.class);
  }
}