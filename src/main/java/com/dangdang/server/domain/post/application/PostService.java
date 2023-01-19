package com.dangdang.server.domain.post.application;

import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.TOWN_NOT_FOUND;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.response.PostResponse;
import com.dangdang.server.domain.post.dto.response.PostSliceResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final TownRepository townRepository;

  public PostService(PostRepository postRepository, TownRepository townRepository) {
    this.postRepository = postRepository;
    this.townRepository = townRepository;
  }

  public PostsSliceResponse findPostsForSlice(PostSliceRequest postSliceRequest,
      Member loginMember) {
    /*
    TODO : Member 정보(town Id 등) 가져오기 기능 완료되면 파라미터 수정
     */
    long townIdSelectedByUser = 1L;
    int level = 1;
    List<Long> adjacency = townRepository.findAdjacencyTownId(
        townIdSelectedByUser, level);
    Slice<Post> posts = postRepository.findPostsByTownIdFetchJoinSortByCreatedAt(
        adjacency,
        PageRequest.of(postSliceRequest.getPage(), postSliceRequest.getSize(),
            Sort.by("createdAt")));
    return PostsSliceResponse.of(
        posts.getContent().stream().map(PostSliceResponse::from).collect(
            Collectors.toList()), posts.hasNext()
    );
  }

  public PostResponse findPostById(Long postId) {
    Post foundPost = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    return PostResponse.from(foundPost);
  }

  @Transactional
  public PostResponse savePost(PostSaveRequest postSaveRequest, Member loginMember) {
    Town foundTown = townRepository.findTownByName(postSaveRequest.getTownName())
        .orElseThrow(() -> new TownNotFoundException(TOWN_NOT_FOUND));
    Post post = PostSaveRequest.toPost(postSaveRequest, loginMember, foundTown);
    Post savedPost = postRepository.save(post);
    return PostResponse.from(savedPost);
  }
}
