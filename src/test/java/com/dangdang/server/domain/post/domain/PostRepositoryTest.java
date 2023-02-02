package com.dangdang.server.domain.post.domain;

import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.likes.domain.LikesRepository;
import com.dangdang.server.domain.likes.domain.entity.Likes;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import java.math.BigDecimal;
import com.dangdang.server.domain.post.domain.entity.PostSearch;
import com.dangdang.server.domain.post.domain.entity.UpdatedPost;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.infrastructure.PostSearchRepositoryImpl;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class PostRepositoryTest {

  @Autowired
  PostRepository postRepository;
  @Autowired
  PostSearchRepositoryImpl postSearchRepositoryImpl;
  @Autowired
  PostSearchRepository postSearchRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TownRepository townRepository;
  @Autowired
  LikesRepository likesRepository;

  Member member;
  Town town;
  Post post;

  void savePost() {
    Member newMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    member = memberRepository.save(newMember);
    Town newTown = new Town("서현동", null, null);
    town = townRepository.save(newTown);

    post = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
        new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, member, town,
        "http://s3.amazonaws.com/test1.png", StatusType.SELLING);

    postRepository.save(post);
  }

  void setUpForSearch() throws Exception {
    Member member = new Member("01064083433", "yb");
    memberRepository.save(member);
    Post post;
    for (int i = 1; i <= 40; i++) {
      //given
      Town town = townRepository.findByName("천호동").get();
      if (i >= 1 && i <= 10) {
        post = new Post("지우개 팝니다.", "한 번도 안쓴 미개봉 지우개입니다. 수량은 " + i + "개씩 팔아요.", Category.디지털기기,
            10000,
            null, null, null, 0, false,
            member, town, "http://s3.amazonaws.com/test1.png", StatusType.SELLING);
      } else if (i >= 11 && i <= 20) {
        post = new Post("지우개 팝니다.", "한 번도 안쓴 미개봉 지우개입니다. 수량은 " + i + "개씩 팔아요.", Category.생활가전,
            20000,
            null, null, null, 0, false,
            member, town, "http://s3.amazonaws.com/test1.png", StatusType.SELLING);
      } else if (i >= 21 && i <= 30) {
        post = new Post("지우개 팝니다.", "한 번도 안쓴 미개봉 지우개입니다. 수량은 " + i + "개씩 팔아요.", Category.유아도서,
            30000,
            null, null, null, 0, false,
            member, town, "http://s3.amazonaws.com/test1.png", StatusType.SELLING);
      } else {
        post = new Post("지우개 팝니다.", "한 번도 안쓴 미개봉 지우개입니다. 수량은 " + i + "개씩 팔아요.", Category.유아동,
            40000,
            null, null, null, 0, false,
            member, town, "http://s3.amazonaws.com/test1.png", StatusType.RESERVED);
      }

      // "image_url"은 기존 테스트 코드의 동작을 보장하기 위한 임의의 image link String 값입니다.
      // when
      postRepository.save(post);
      postSearchRepository.save(PostSearch.from(UpdatedPost.from(post)));
    }

    //given
    List<Post> posts = postRepository.findAll();
    List<Long> adjacency = posts.stream().map(post -> post.getTown().getId())
        .collect(Collectors.toList());
    int pageNum = 0;
    int size = 2;
    // when
    Slice<Post> findPosts = postRepository.findPostsByTownIdFetchJoinSortByCreatedAt(adjacency,
        PageRequest.of(pageNum, size, Sort.by("createdAt").descending()));
    //then
    assertThat(findPosts).hasSize(2);
    assertThat(findPosts.getContent().get(0).getCreatedAt())
        .isAfter(findPosts.getContent().get(1).getCreatedAt());
  }

  @Test
  @DisplayName("게시글 상세 조회시 좋아요를 함께 가져올 수 있다.")
  public void getPostDetailForLikesTest() {
    savePost();
    //given
    Likes likes = new Likes(post, member);
    likesRepository.save(likes);
    likes.addLikes();

    //then
    Post foundPost = postRepository.findPostDetailById(post.getId())
        .orElseThrow(() -> new PostNotFoundException(
            POST_NOT_FOUND));
    Assertions.assertThat(foundPost.getLikes().size()).isEqualTo(1);

  }
  @Nested
  @DisplayName("게시글 검색 테스트")
  class PostSearchTest {
    setUpForSearch();

    @Nested
    @DisplayName("최소, 최대 가격 입력 여부에 따라")
    class MinMaxPriceTest {

      @Test
      @DisplayName("최소 가격이 없으면 최대 가격만으로 검색할 수 있다.")
      public void searchBySearchOptionWithoutMinPrice() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            20000L, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(20);
      }

      @Test
      @DisplayName("최대 가격이 없으면 최소 가격만으로 검색할 수 있다.")
      public void searchBySearchOptionWithoutMaxPrice() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, 40000L,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(10);
      }

      @Test
      @DisplayName("최소 가격, 최대 가격이 모두 없으면 전체 가격으로 검색할 수 있다.")
      public void searchBySearchOptionWithoutMinMaxPrice() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(40);
      }
    }

    @Nested
    @DisplayName("카테고리입력 여부에 따라")
    class CategoryTest {

      @Test
      @DisplayName("카테고리가 없으면 전체 카테고리로 검색할 수 있다.")
      public void searchBySearchOptionWithoutCategory() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(40);
      }

      @Test
      @DisplayName("카테고리 여러개에 해당하는 검색할 수 있다.")
      public void searchBySearchOptionWithCategories() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            List.of(Category.디지털기기, Category.유아동), null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(20);
      }
    }

    @Nested
    @DisplayName("거래 가능 상품만 보기 여부에 따라")
    class TransactionAvailableTest {

      @Test
      @DisplayName("거래 가능한 상품만 검색할 수 있다.")
      public void searchBySearchOptionWithOnlyTransactionAvailable() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, true);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(30);
      }

      @Test
      @DisplayName("거래 가능하지 않은 상품도 검색할 수 있다.")
      public void searchBySearchOptionWithoutOnlyTransactionAvailable() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(40);
      }
    }

    @Nested
    @DisplayName("게시글 제목이나 내용에 검색어와 일치하는 게시글이 있으면")
    class TitleOrContentSearchTest {

      @Test
      @DisplayName("제목이 일치하는 결과를 볼 수 있다.")
      public void searchByTitleWithSearchOption() throws Exception {
        //given
        String query = "지우개";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(40);
      }

      @Test
      @DisplayName("내용이 일치하는 결과를 볼 수 있다.")
      public void searchByContentWithSearchOption() throws Exception {
        //given
        String query = "미개봉";
        PostSearchOptionRequest postSearchOption = new PostSearchOptionRequest(
            null, null,
            null, 1, false);
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 6L, 15L);
        // when
        Slice<PostSearch> posts = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
            postSearchOption, ids, PageRequest.of(0, 50));
        //then
        assertThat(posts.getContent()).hasSize(40);
      }

    }
  }
}