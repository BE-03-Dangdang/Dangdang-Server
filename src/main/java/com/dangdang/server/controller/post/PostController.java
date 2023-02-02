package com.dangdang.server.controller.post;

import static com.dangdang.server.global.exception.ExceptionCode.POST_STATUS_IS_NULL;

import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.dto.request.PostLikeRequest;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.NullParameterException;
import com.dangdang.server.global.aop.CurrentUserId;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public PostsSliceResponse findAll(
      @ModelAttribute @Valid PostSliceRequest postSliceRequest,
      Long memberId,
      BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      throw new MinParameterException(SLICE_PARAMETER_UNDER_ZERO);
    }
    return postService.findPostsForSlice(postSliceRequest, memberId);
  }

  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public PostDetailResponse updatePostStatus(@PathVariable("id") long postId,
      @RequestBody @Valid PostUpdateStatusRequest postUpdateStatusRequest,
      Long memberId, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new NullParameterException(POST_STATUS_IS_NULL);
    }
    return postService.updatePostStatus(postId, postUpdateStatusRequest, memberId);
  }

  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/search")
  public PostsSliceResponse search(@Valid PostSearchOptionRequest searchOption,
      @RequestParam("query") @NotBlank String query, @Valid PostSliceRequest postSliceRequest,
      Long memberId) {
    return postService.search(query, searchOption, memberId, postSliceRequest);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailResponse> findPostById(@PathVariable("postId") long postId) {
    postService.viewUpdate(postId);
    PostDetailResponse postDetailResponse = postService.findPostDetailById(postId);
    return ResponseEntity.ok(postDetailResponse);
  }

  @PostMapping("/likes")
  public ResponseEntity<Void> updatePostLike(@RequestBody PostLikeRequest postLikeRequest) {
    postService.clickLikes(postLikeRequest);
    return ResponseEntity.ok().build();
  }
}
