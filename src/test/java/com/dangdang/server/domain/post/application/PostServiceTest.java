package com.dangdang.server.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
  private PostImageRepository postImageRepository;

  @Test
  @DisplayName("게시글을 작성할 수 있다.")
  void postSaveTest() {
    Member loginMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    memberRepository.save(loginMember);
    Town town = new Town("서현동", null, null);
    townRepository.save(town);

    PostImageRequest postImageRequest = new PostImageRequest(List.of("url1", "url2"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        postImageRequest);
    PostResponse savedPostResponse = postService.savePost(postSaveRequest, loginMember);

    PostDetailResponse postDetailResponse = postService.findPostDetailById(
        savedPostResponse.getId());
    assertThat(savedPostResponse).usingRecursiveComparison()
        .isEqualTo(postDetailResponse.getPostResponse());
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
    Member loginMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    memberRepository.save(loginMember);
    Town town = new Town("서현동", null, null);
    townRepository.save(town);

    PostImageRequest postImageRequest = new PostImageRequest(Arrays.asList("url1", "url2"));

    PostSaveRequest postSaveRequest = new PostSaveRequest("title1", "content1", Category.디지털기기,
        1000, "서현동 코지카페", BigDecimal.valueOf(123L), BigDecimal.valueOf(123L), false, "서현동",
        postImageRequest);

    PostResponse savedPostResponse = postService.savePost(postSaveRequest, loginMember);

    PostDetailResponse foundPost = postService.findPostDetailById(savedPostResponse.getId());
    assertThat(foundPost).isNotNull();
    assertThat(foundPost.getImageUrls()).hasSize(2);
    assertThat(foundPost.getImageUrls()).usingRecursiveComparison();
  }


}