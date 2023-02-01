package com.dangdang.server.domain.postImage.domain;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.domain.entity.PostImage;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostImageRepositoryTest {

  @Autowired
  PostImageRepository postImageRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TownRepository townRepository;
  @Autowired
  PostRepository postRepository;

  Member member;
  Town town;
  Post post;

  @BeforeEach
  void setup() {
    //given
    Member newMember = new Member("테스트 멤버", "01012341234", "testImgUrl");
    member = memberRepository.save(newMember);

    town = townRepository.findByName("천호동").get();
    Post newPost = new Post("title1", "content1", Category.디지털기기,
        1000, null, null, null, 0,
        false, member, town, null, StatusType.SELLING);
    post = postRepository.save(newPost);
  }

  @Test
  void deleteTest() {
    String url1 = "http://s3.amazonaws.com/test1.png";
    String url2 = "http://s3.amazonaws.com/test2.png";

    PostImage postImageOne = new PostImage(post, url1);
    PostImage postImageTwo = new PostImage(post, url2);

    postImageRepository.save(postImageOne);
    postImageRepository.save(postImageTwo);

    List<String> imageUrls = postImageRepository.findPostImagesByPostId(post.getId()).stream()
        .map(PostImage::getUrl)
        .collect(Collectors.toList());
    Assertions.assertThat(imageUrls).contains(url1);
    Assertions.assertThat(imageUrls).contains(url2);

    postImageRepository.deletePostImageByPostId(post.getId());
    List<PostImage> postImages = postImageRepository.findPostImagesByPostId(post.getId());
    Assertions.assertThat(postImages.size()).isEqualTo(0);
  }

}