package com.dangdang.server.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.likes.domain.LikesRepository;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostLikeRequest;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.exception.UrlInValidException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
class PostServiceTest {

  @Autowired
  PostService postService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TownRepository townRepository;
  @Autowired
  PostImageRepository postImageRepository;

  @Autowired
  EntityManager entityManager;

  @Value("${s3.bucket}")
  String bucketName;
  @Value("${cloud.aws.region.static}")
  private String region;

  Member member;
  Town town;
  PostSaveRequest postSaveRequest;

  @Autowired
  private SaveClassForViewUpdate saveClassForViewUpdate;

  @Autowired
  private LikesRepository likeRepository;

  @TestConfiguration
  static class testConfig {

    @Bean
    public SaveClassForViewUpdate innerClass() {
      return new SaveClassForViewUpdate();
    }
  }

  static class SaveClassForViewUpdate {

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TownRepository townRepository;

    Member innerMember;
    Town innerTown;
    Post innerPost;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Post save() {
      innerMember = new Member("01033334444", "testImgUrl", "테스트 멤버");
      memberRepository.save(innerMember);
      innerTown = new Town("테스트동2", null, null);
      townRepository.save(innerTown);

      innerPost = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
          new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, innerMember, innerTown,
          "http://s3.amazonaws.com/test1.png", StatusType.SELLING);

      return postRepository.save(innerPost);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAfterTest() {
      memberRepository.deleteById(innerMember.getId());
      townRepository.deleteById(innerTown.getId());
      postRepository.deleteById(innerPost.getId());
    }
  }

  void setup() {
    Member newMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    member = memberRepository.save(newMember);
    Town newTown = new Town("서현동", null, null);
    town = townRepository.save(newTown);

    PostImageRequest postImageRequest = new PostImageRequest(Arrays.asList(
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test2.png",
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test3.png"));

    postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        postImageRequest);
  }

  @Test
  @DisplayName("게시글을 작성할 수 있다.")
  void postSaveTest() {
    setup();

    PostDetailResponse postDetailResponse = postService.savePost(postSaveRequest, member);
    System.out.println("######## posResponseId:" + postDetailResponse.postResponse().getId());
    PostDetailResponse resultDetailResponse = postService.findPostDetailById(
        postDetailResponse.postResponse().getId());
    assertThat(postDetailResponse.postResponse().getId()).usingRecursiveComparison()
        .isEqualTo(resultDetailResponse.postResponse().getId());
  }

  @Test
  @DisplayName("게시글 조회시 해당하는 id값이 없다면 PostNotFoundException이 발생한다.")
  void findPostByIdInCorrect() {
    Long wrongId = 9999L;

    assertThatThrownBy(() -> postService.findPostDetailById(wrongId)).isInstanceOf(
        PostNotFoundException.class);
  }

  @Test
  @DisplayName("게시글을 상세 조회 할 수 있다.")
  void findPostDetailById() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest, member);
    PostDetailResponse foundPost = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().getId());

    assertThat(foundPost).isNotNull();
    assertThat(foundPost.imageUrls()).hasSize(2);
    assertThat(foundPost.imageUrls()).usingRecursiveComparison();
  }

  @Test
  @DisplayName("게시글을 상세 조회시 좋아요 횟수를 가져올 수 있다.")
  void findPostDetailByIdV2() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest, member);
    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().getId(), member.getId()));

    PostDetailResponse foundPost = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().getId());

    assertThat(foundPost.postResponse().getLikeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("게시글 좋아용를 이미 누른 유저가 한번 더 누른 경우 취소된다.")
  void cancelLikes() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest, member);
    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().getId(), member.getId()));

    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().getId(), member.getId()));

    entityManager.flush();
    entityManager.clear();

    PostDetailResponse foundPost = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().getId());

    assertThat(foundPost.postResponse().getLikeCount()).isEqualTo(0);
  }

  @Test
  @Order(0)
  @DisplayName("게시글을 여러명이 동시에 접속하여도 view값의 동시성을 확보하여 update가 가능하다.")
  void multiThreadForViewUpdateTest() {
    try {
      Post savedPost = saveClassForViewUpdate.save();
      int threadCount = 10;
      ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
      CountDownLatch latch = new CountDownLatch(threadCount);

      for (int i = 0; i < threadCount; i++) {
        executorService.execute(() -> {
          postService.viewUpdate(savedPost.getId());
          latch.countDown();
        });
      }
      latch.await();
      PostDetailResponse resultPostResponse = postService.findPostDetailById(
          savedPost.getId());
      Assertions.assertThat(resultPostResponse.postResponse().getView())
          .isEqualTo(threadCount);
    } catch (Exception e) {
      log.info("####### viewUpTest Exception: {}, {} ", e, e.getMessage());
    } finally {
      saveClassForViewUpdate.deleteAfterTest();
    }
  }

  @Test
  @DisplayName("게시글을 상세 조회 시 열어볼 수 있는 이미지 링크를 제공할 수 있다.")
  void findPostDetailByIdOpenImageLink() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest, member);

    PostDetailResponse foundPost = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().getId());
    Assertions.assertThat(foundPost.imageUrls()).hasSize(2);
  }

  @Test
  @DisplayName("게시글 1개 조회 시 이미지 URL이 잘못되면 UrlInvalidException이 발생한다.")
  void findPostDetailByIdThrowUrlInvalidException() {
    setup();
    PostImageRequest wrongPostImageRequest = new PostImageRequest(Arrays.asList("url1", "url2"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        wrongPostImageRequest);

    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest, member);

    assertThatThrownBy(
        () -> postService.findPostDetailById(savedPostDetailResponse.postResponse().getId()))
        .isInstanceOf(UrlInValidException.class);
  }
}