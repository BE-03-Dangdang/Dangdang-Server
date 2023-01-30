package com.dangdang.server.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.exception.UrlInValidException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

  @Value("${s3.bucket}")
  String bucketName;
  @Value("${cloud.aws.region.static}")
  private String region;

  private Member loginMember;

  private PostDetailResponse savedPostResponse;

  @BeforeEach
  void setUp() {
    loginMember = new Member("테스트 멤버", "01012341234", "yb");
    memberRepository.save(loginMember);

    Town town = townRepository.findByName("천호동")
        .orElseThrow(() -> new TownNotFoundException(ExceptionCode.TOWN_NOT_FOUND));

    MemberTown memberTown = new MemberTown(loginMember, town);
    memberTownRepository.save(memberTown);

    PostImageRequest postImageRequest = new PostImageRequest(Arrays.asList(
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test2.png",
        "https://" + bucketName + ".s3." + region + ".amazonaws.com/post-image/test3.png"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("맛있는 커피팝니다.", "아메리카노가 단돈 1000원!",
        Category.디지털기기, 1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false,
        "서현동", postImageRequest);
    savedPostResponse = postService.savePost(postSaveRequest, loginMember.getId());
  }

  @Test
  @DisplayName("게시글을 작성할 수 있다.")
  void postSaveTest() {
    PostImageRequest postImageRequest = new PostImageRequest(
        List.of("http://s3.amazonaws.com/test1.png", "http://s3.amazonaws.com/test2.png"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        postImageRequest);
    PostDetailResponse savedPostResponse = postService.savePost(postSaveRequest,
        loginMember.getId());

    PostDetailResponse foundPost = postService.findPostDetailById(savedPostResponse.getPostId());
    assertThat(savedPostResponse.getPostResponse()).usingRecursiveComparison()
        .isEqualTo(foundPost.getPostResponse());
    assertThat(savedPostResponse.getMember()).usingRecursiveComparison()
        .isEqualTo(foundPost.getMember());
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
    PostDetailResponse foundPost = postService.findPostDetailById(savedPostResponse.getPostId());
    assertThat(foundPost).isNotNull();
    assertThat(foundPost.getImageUrls()).hasSize(2);
  }

  @Test
  @DisplayName("게시글을 상세 조회 시 열어볼 수 있는 이미지 링크를 제공할 수 있다.")
  void findPostDetailByIdOpenImageLink() {
    PostDetailResponse foundPost = postService.findPostDetailById(savedPostResponse.getPostId());
    Assertions.assertThat(foundPost.getImageUrls()).hasSize(2);
  }

  @Test
  @DisplayName("게시글 1개 조회 시 이미지 URL이 잘못되면 UrlInvalidException이 발생한다.")
  void findPostDetailByIdThrowUrlInvalidException() {
    PostImageRequest wrongPostImageRequest = new PostImageRequest(Arrays.asList("url1", "url2"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        wrongPostImageRequest);

    PostDetailResponse savedPostResponse = postService.savePost(postSaveRequest,
        loginMember.getId());

    assertThatThrownBy(
        () -> postService.findPostDetailById(savedPostResponse.getPostId())).isInstanceOf(
        UrlInValidException.class);
  }

  @Test
  @DisplayName("검색어와 각종 파라미터를 사용해서 검색할 수 있다.")
  public void searchWithQueryAndOptions() throws Exception {
    //given
    String query = "커피";
    PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(List.of(Category.디지털기기),
        0L, 40000L, 1, true);

    // when
    PostsSliceResponse posts = postService.search(query, postSearchOption, loginMember.getId(),
        new PostSliceRequest(0, 10));
    //then
    Assertions.assertThat(posts.getPostSliceResponses()).hasSize(1);
  }
}