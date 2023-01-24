package com.dangdang.server.domain.post.application;

import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.TOWN_NOT_FOUND;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberUnmatchedAuthorException;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.postImage.application.PostImageService;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final PostImageService postImageService;
  private final TownRepository townRepository;

  public PostService(PostRepository postRepository, PostImageService postImageService,
      TownRepository townRepository) {
    this.postRepository = postRepository;
    this.postImageService = postImageService;
    this.townRepository = townRepository;
  }

  public PostsSliceResponse findPostsForSlice(PostSliceRequest postSliceRequest,
      Member loginMember) {
    /*
    TODO : Member 정보(town Id 등) 가져오기 기능 완료되면 파라미터 수정
     */
//     long townIdSelectedByUser = 1L;
//     int level = 1;
//     List<Long> adjacency = townRepository.findAdjacencyTownIdByRangeTypeAndTownId(
//         townIdSelectedByUser, level);
//     Slice<Post> posts = postRepository.findPostsByTownIdFetchJoinSortByCreatedAt(
//         adjacency,
//         PageRequest.of(postSliceRequest.getPage(), postSliceRequest.getSize(),
//             Sort.by("createdAt")));
//     return PostsSliceResponse.of(
//         posts.getContent().stream().map(PostSliceResponse::from).collect(
//             Collectors.toList()), posts.hasNext()
//     );
    return null;
  }

  @Transactional
  public PostDetailResponse savePost(PostSaveRequest postSaveRequest, Member loginMember) {
    Town foundTown = townRepository.findByName(postSaveRequest.getTownName())
        .orElseThrow(() -> new TownNotFoundException(TOWN_NOT_FOUND));
    Post post = PostSaveRequest.toPost(postSaveRequest, loginMember, foundTown);
    Post savedPost = postRepository.save(post);
    List<String> imageUrls = postImageService.savePostImage(savedPost,
        postSaveRequest.getPostImageRequest());

    return PostDetailResponse.from(savedPost, imageUrls);
  }

  public PostDetailResponse findPostDetailById(Long postId) {
    Post foundPost = postRepository.findPostDetailById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    List<String> imageUrls = postImageService.findPostImagesByPostId(postId);

    return PostDetailResponse.from(foundPost, imageUrls);
  }

  @Transactional
  public PostDetailResponse updatePostStatus(Long postId, PostUpdateStatusRequest postUpdateStatusRequest, Long authorId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    if(!Objects.equals(post.getMemberId(), authorId)) {
      throw new MemberUnmatchedAuthorException(ExceptionCode.MEMBER_UNMATCH_AUTHOR);
    }

    post.changeStatus(postUpdateStatusRequest.status());
    List<String> imageUrls = postImageService.findPostImagesByPostId(postId);
    return PostDetailResponse.from(post, imageUrls);
  }
}
