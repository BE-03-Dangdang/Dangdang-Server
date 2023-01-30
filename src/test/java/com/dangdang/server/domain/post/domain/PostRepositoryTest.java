package com.dangdang.server.domain.post.domain;

import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.likes.domain.LikesRepository;
import com.dangdang.server.domain.likes.domain.entity.Likes;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

  @Autowired
  PostRepository postRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TownRepository townRepository;
  @Autowired
  LikesRepository likesRepository;

  Member member;
  Town town;
  Post post;

  @BeforeEach
  void setup() {
    Member newMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    member = memberRepository.save(newMember);
    Town newTown = new Town("서현동", null, null);
    town = townRepository.save(newTown);

    post = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
        new BigDecimal("126.1111"), new BigDecimal("36.111111"), 0, false, member, town,
        "http://s3.amazonaws.com/test1.png", StatusType.SELLING);

    postRepository.save(post);
  }

  @Test
  @DisplayName("게시글을 페이징 처리 할 수 있다.")
  public void getAllPosts() throws Exception {

    for (int i = 1; i <= 4; i++) {
      //given
      Member member = new Member("테스트 멤버" + i, "01012341234", "testImgUrl");
      memberRepository.save(member);
      Town town = new Town("테스트 동" + i, null, null);
      townRepository.save(town);

      Post post = new Post("제목" + i, "내용" + i, Category.디지털기기, 20000,
          null, null, null, 0, false,
          member, town, "image_url", StatusType.SELLING);
      // "image_url"은 기존 테스트 코드의 동작을 보장하기 위한 임의의 image link String 값입니다.
      // when
      postRepository.save(post);
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
    Assertions.assertThat(findPosts).hasSize(2);
    Assertions.assertThat(findPosts.getContent().get(0).getCreatedAt())
        .isAfter(findPosts.getContent().get(1).getCreatedAt());
  }

  @Test
  @DisplayName("게시글 상세 조회시 좋아요를 함께 가져올 수 있다.")
  public void getPostDetailForLikesTest() {
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
}