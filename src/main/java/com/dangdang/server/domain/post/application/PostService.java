package com.dangdang.server.domain.post.application;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.response.PostSliceResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.town.domain.TownRepository;
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
    List<Long> adjacency = townRepository.findAdjacencyTownIdByRangeTypeAndTownId(
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

}
