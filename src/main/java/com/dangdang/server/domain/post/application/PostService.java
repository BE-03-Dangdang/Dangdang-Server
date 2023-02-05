package com.dangdang.server.domain.post.application;

import static com.dangdang.server.global.exception.ExceptionCode.MEMBER_NOT_FOUND;
import static com.dangdang.server.global.exception.ExceptionCode.MEMBER_UNMATCH_AUTHOR;
import static com.dangdang.server.global.exception.ExceptionCode.NO_ACTIVE_TOWN;
import static com.dangdang.server.global.exception.ExceptionCode.POST_NOT_FOUND;

import com.dangdang.server.domain.likes.domain.LikesRepository;
import com.dangdang.server.domain.likes.domain.entity.Likes;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.member.exception.MemberUnmatchedAuthorException;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.exception.MemberTownNotFoundException;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.UpdatedPostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.domain.entity.PostSearch;
import com.dangdang.server.domain.post.domain.entity.UpdatedPost;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostSliceResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.post.infrastructure.PostSearchRepositoryImpl;
import com.dangdang.server.domain.postImage.application.PostImageService;
import com.dangdang.server.domain.town.application.TownService;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.dto.request.AdjacentTownRequest;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final PostImageService postImageService;
  private final LikesRepository likesRepository;
  private final MemberRepository memberRepository;

  private final MemberTownRepository memberTownRepository;
  private final TownService townService;
  private final UpdatedPostRepository updatedPostRepository;
  private final PostSearchRepositoryImpl postSearchRepositoryImpl;

  public PostService(PostRepository postRepository, PostImageService postImageService,
      MemberTownRepository memberTownRepository, TownService townService,
      UpdatedPostRepository updatedPostRepository,
      PostSearchRepositoryImpl postSearchRepositoryImpl, LikesRepository likesRepository,
      MemberRepository memberRepository) {
    this.postRepository = postRepository;
    this.postImageService = postImageService;
    this.memberTownRepository = memberTownRepository;
    this.townService = townService;
    this.updatedPostRepository = updatedPostRepository;
    this.postSearchRepositoryImpl = postSearchRepositoryImpl;
    this.likesRepository = likesRepository;
    this.memberRepository = memberRepository;
  }

  public PostsSliceResponse findPostsForSlice(PostSliceRequest postSliceRequest,
      Long loginMemberId) {
    MemberTown memberTown = memberTownRepository.findActiveMemberTownByMember(loginMemberId)
        .orElseThrow(() -> new MemberTownNotFoundException(NO_ACTIVE_TOWN));
    Long townId = memberTown.getTown().getId();
    AdjacentTownRequest adjacentTownRequest = new AdjacentTownRequest(townId,
        memberTown.getRangeType());
    List<Long> adjacency = townService.findAdjacentTownIds(adjacentTownRequest);
    Slice<Post> posts = postRepository.findPostsByTownIdFetchJoinSortByCreatedAt(adjacency,
        PageRequest.of(postSliceRequest.getPage(), postSliceRequest.getSize(),
            Sort.by("createdAt")));
    return PostsSliceResponse.of(
        posts.getContent().stream().map(PostSliceResponse::from).collect(Collectors.toList()),
        posts.hasNext());
  }

  @Transactional
  public PostDetailResponse savePost(PostSaveRequest postSaveRequest, Long memberId) {
    MemberTown memberTown = memberTownRepository.findActiveMemberTownByMember(memberId)
        .orElseThrow(() -> new MemberTownNotFoundException(NO_ACTIVE_TOWN));
    Member member = memberTown.getMember();
    Town town = memberTown.getTown();
    Post post = PostSaveRequest.toPost(postSaveRequest, member, town);
    Post savedPost = postRepository.save(post);

    UpdatedPost updatedPost = UpdatedPost.from(savedPost);
    updatedPostRepository.save(updatedPost);

    List<String> imageUrls = postImageService.savePostImage(savedPost,
        postSaveRequest.postImageRequest());

    return PostDetailResponse.from(savedPost, member, imageUrls);
  }

  public PostDetailResponse findPostDetailById(Long postId) {
    Post foundPost = postRepository.findPostDetailById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    List<String> imageUrls = postImageService.findImageUrlsByPostId(postId);
    return PostDetailResponse.from(foundPost, foundPost.getMember(), imageUrls);
  }

  @Transactional
  public PostDetailResponse updatePostStatus(Long postId,
      PostUpdateStatusRequest postUpdateStatusRequest, Long authorId) {
    Post post = postRepository.findPostDetailById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));
    if (!post.getMemberId().equals(authorId)) {
      throw new MemberUnmatchedAuthorException(MEMBER_UNMATCH_AUTHOR);
    }

    post.changeStatus(postUpdateStatusRequest.status());
    List<String> imageUrls = postImageService.findImageUrlsByPostId(postId);
    return PostDetailResponse.from(post, post.getMember(), imageUrls);
  }

  @Transactional
  public PostDetailResponse updatePost(PostUpdateRequest postUpdateRequest, Long loginMemberId) {
    Post foundPost = postRepository.findById(postUpdateRequest.id())
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    if (!loginMemberId.equals(foundPost.getMemberId())) {
      throw new MemberUnmatchedAuthorException(MEMBER_UNMATCH_AUTHOR);
    }

    List<String> imageUrls = postUpdateRequest.postImageRequest().urls();
    postImageService.renewPostImage(foundPost, imageUrls);
    foundPost.changePost(PostUpdateRequest.to(postUpdateRequest));

    return PostDetailResponse.from(foundPost, foundPost.getMember(), imageUrls);
  }

  public PostsSliceResponse search(String query, PostSearchOptionRequest postSearchOption,
      Long memberId, PostSliceRequest postSliceRequest) {
    MemberTown memberTown = memberTownRepository.findActiveMemberTownByMember(memberId)
        .orElseThrow(() -> new MemberTownNotFoundException(NO_ACTIVE_TOWN));

    List<Long> adjacentTownIds = townService.findAdjacentTownWithRangeLevel(
        memberTown.getTownName(), String.valueOf(postSearchOption.rangeLevel()));

    Slice<PostSearch> postSlice = postSearchRepositoryImpl.searchBySearchOptionSlice(query,
        postSearchOption, adjacentTownIds,
        PageRequest.of(postSliceRequest.getPage(), postSliceRequest.getSize() + 1,
            Sort.by("createdAt").descending()));
    return PostsSliceResponse.of(
        postSlice.getContent().stream().map(PostSliceResponse::from).collect(Collectors.toList()),
        postSlice.hasNext());
  }

  @Transactional
  public void uploadToES() {
    List<UpdatedPost> updatedPosts = updatedPostRepository.findAll();
    if (updatedPosts.isEmpty()) {
      throw new PostNotFoundException(ExceptionCode.UPDATABLE_POST_NOT_EXIST);
    }

    List<PostSearch> postSearches = updatedPosts.stream().map(PostSearch::from).toList();
    postSearchRepositoryImpl.bulkInsertOrUpdate(postSearches);
    updatedPostRepository.deleteAll();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void viewUpdate(Long postId) {
    Post foundPost = postRepository.findByIdForUpdate(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    foundPost.upView();
  }

  @Transactional
  public void clickLikes(Long postId, Long memberId) {
    Post foundPost = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

    Member foundMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));

    likesRepository.findByPostIdAndMemberId(postId, memberId)
        .ifPresentOrElse(likes -> likesRepository.delete(likes), () -> {
          Likes saveLikes = likesRepository.save(new Likes(foundPost, foundMember));
          saveLikes.addLikes();
        });
  }
}
