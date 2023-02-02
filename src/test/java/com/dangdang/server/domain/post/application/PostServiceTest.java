package com.dangdang.server.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostResponse;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostLikeRequest;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.exception.UrlInValidException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
  MemberTownRepository memberTownRepository;

  @Autowired
  EntityManager entityManager;

  @Value("${s3.bucket}")
  String bucketName;
  @Value("${cloud.aws.region.static}")
  private String region;

  Member member;
  Town town;
  PostSaveRequest postSaveRequest;
  MemberTown memberTown;

  @Autowired
  private SaveClassForViewUpdate saveClassForViewUpdate;

  private PostDetailResponse savedPostResponse;

  void setup() {
    Member newMember = new Member("01077778888", "yb");
    member = memberRepository.save(newMember);
    town = townRepository.findByName("천호동")
        .orElseThrow(() -> new TownNotFoundException(ExceptionCode.TOWN_NOT_FOUND));

    MemberTown newMemberTown = new MemberTown(this.member, town);
    memberTown = memberTownRepository.save(newMemberTown);

    PostImageRequest postImageRequest = new PostImageRequest(Arrays.asList(
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test2.png",
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test3.png"));

    postSaveRequest = new PostSaveRequest("맛있는 커피팝니다.", "아메리카노가 단돈 1000원!",
        Category.디지털기기, 1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false,
        "서현동", postImageRequest);
    savedPostResponse = postService.savePost(postSaveRequest, this.member.getId());
    postService.uploadToES();
  }

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
    @Autowired
    MemberTownRepository memberTownRepository;

    Member innerMember;
    Town innerTown;
    MemberTown innerMemberTown;
    Post innerPost;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Post save() {
      innerMember = new Member("01045675544", "testImgUrl", "테스트 멤버");
      memberRepository.save(innerMember);
      innerTown = townRepository.findByName("천호동").get();
      townRepository.save(innerTown);

      MemberTown newMemberTown = new MemberTown(innerMember, innerTown);
      innerMemberTown = memberTownRepository.save(newMemberTown);

      innerPost = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
          new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, innerMember, innerTown,
          "http://s3.amazonaws.com/test1.png", StatusType.SELLING);

      return postRepository.save(innerPost);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAfterTest() {
      memberRepository.deleteById(innerMember.getId());
      memberTownRepository.deleteById(innerMemberTown.getId());
      postRepository.deleteById(innerPost.getId());
    }
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
      Assertions.assertThat(resultPostResponse.postResponse().view())
          .isEqualTo(threadCount);
    } catch (Exception e) {
      log.info("####### viewUpTest Exception: {}, {} ", e, e.getMessage());
    } finally {
      saveClassForViewUpdate.deleteAfterTest();
    }
  }

  @Test
  @DisplayName("게시글을 작성할 수 있다.")
  void postSaveTest() {
    setup();
    PostDetailResponse postDetailResponse = postService.savePost(postSaveRequest,
        member.getId());
    PostDetailResponse foundPostDetailResponse = postService.findPostDetailById(
        postDetailResponse.postId());
    assertThat(postDetailResponse.postResponse()).usingRecursiveComparison()
        .isEqualTo(foundPostDetailResponse.postResponse());
    assertThat(postDetailResponse.memberResponse()).usingRecursiveComparison()
        .isEqualTo(foundPostDetailResponse.memberResponse());
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
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest,
        member.getId());
    PostDetailResponse foundPost = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().id());

    assertThat(foundPost).isNotNull();
    assertThat(foundPost.imageUrls()).hasSize(2);
    assertThat(foundPost.imageUrls()).usingRecursiveComparison();
  }

  @Test
  @DisplayName("게시글을 상세 조회시 좋아요 횟수를 가져올 수 있다.")
  void findPostDetailByIdV2() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest,
        member.getId());
    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().id(), member.getId()));

    PostDetailResponse foundDetailResponse = postService.findPostDetailById(
        savedPostDetailResponse.postId());

    assertThat(foundDetailResponse.postResponse().likeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("게시글 좋아용를 이미 누른 유저가 한번 더 누른 경우 취소된다.")
  void cancelLikes() {
    setup();
    PostDetailResponse savedPostDetailResponse = postService.savePost(postSaveRequest,
        member.getId());
    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().id(), member.getId()));

    postService.clickLikes(
        new PostLikeRequest(savedPostDetailResponse.postResponse().id(), member.getId()));

    entityManager.flush();
    entityManager.clear();

    PostDetailResponse foundPostDetailResponse = postService.findPostDetailById(
        savedPostDetailResponse.postResponse().id());

    assertThat(foundPostDetailResponse.postResponse().likeCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("게시글을 상세 조회 시 열어볼 수 있는 이미지 링크를 제공할 수 있다.")
  void findPostDetailByIdOpenImageLink() {
    setup();
    PostDetailResponse foundPost = postService.findPostDetailById(savedPostResponse.postId());
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

    PostDetailResponse savedPostResponse = postService.savePost(postSaveRequest,
        member.getId());

    assertThatThrownBy(
        () -> postService.findPostDetailById(savedPostResponse.postId())).isInstanceOf(
        UrlInValidException.class);
  }

  @Test
  @DisplayName("검색어와 각종 파라미터를 사용해서 ES로 검색할 수 있다.")
  public void searchWithQueryAndOptionsES() throws Exception {
    setup();
    //given
    String query = "아메리카노";
    PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(List.of(Category.디지털기기),
        0L, 40000L, 1, true);

    // when
    PostsSliceResponse posts = postService.search(query, postSearchOption, member.getId(),
        new PostSliceRequest(0, 10));
    //then
    Assertions.assertThat(posts.getPostSliceResponses()).hasSizeGreaterThan(0);
  }

  @Test
  @DisplayName("게시글을 수정할 수 있다.")
  void updatePostTest() {
    //given
    Member newMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    Member member = memberRepository.save(newMember);

    PostImageRequest postImageRequest = new PostImageRequest(
        Arrays.asList("http://s3.amazonaws.com/test1.png", "http://s3.amazonaws.com/test2.png"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "천호동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "천호동",
        postImageRequest);

    PostDetailResponse savedPostResponse = postService.savePost(postSaveRequest, member.getId());
    PostResponse postResponse = savedPostResponse.postResponse();

    String updateTitle = "updateTitle";
    String updateContent = "updateContent";
    Integer updatePrice = 2000;
    Category updateCategory = Category.생활가전;
    String updateDesiredPlaceName = "길거리";
    BigDecimal updateLongitude = BigDecimal.valueOf(127.0000);
    BigDecimal updateLatitude = BigDecimal.valueOf(36.0000);
    String updateUrlOne = "http://s3.amazonaws.com/updateTest1";
    String updateUrlTwo = "http://s3.amazonaws.com/updateTest2";
    PostImageRequest updatePostImageRequest = new PostImageRequest(
        Arrays.asList(updateUrlOne, updateUrlTwo));

    //when
    PostUpdateRequest postUpdateRequest = new PostUpdateRequest(postResponse.id(),
        updateTitle, updateContent,
        updateCategory, updatePrice,
        updateDesiredPlaceName,
        updateLongitude,
        updateLatitude, false, updatePostImageRequest);

    PostDetailResponse postDetailResponse = postService.updatePost(postUpdateRequest, member);

    //then
    PostDetailResponse resultPostDetail = postService.findPostDetailById(postResponse.id());

    assertThat(postDetailResponse.postResponse()).usingRecursiveComparison()
        .isEqualTo(resultPostDetail.postResponse());
    List<String> imageUrls = postImageRepository.findPostImagesByPostId(postResponse.id())
        .stream().map(
            PostImage::getUrl).collect(Collectors.toList());
    Assertions.assertThat(imageUrls).contains(updateUrlOne, updateUrlTwo);
  }
}