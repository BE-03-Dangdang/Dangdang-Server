package com.dangdang.server.controller.post;

import static com.dangdang.server.global.exception.ExceptionCode.POST_STATUS_IS_NULL;
import static com.dangdang.server.global.exception.ExceptionCode.SLICE_PARAMETER_UNDER_ZERO;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateRequest;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.MinParameterException;
import com.dangdang.server.domain.post.exception.NullParameterException;
import com.dangdang.server.global.aop.CurrentUserId;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PostDetailResponse> savePost(
      @RequestBody PostSaveRequest postSaverequest, Long memberId) {

    PostDetailResponse postDetailResponse = postService.savePost(postSaverequest, memberId);

    return ResponseEntity.created(URI.create("/posts/" + postDetailResponse.postId()))
        .body(postDetailResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostDetailResponse> findPostDetailById(@PathVariable("id") long postId) {
    PostDetailResponse postDetailResponse = postService.findPostDetailById(postId);
    return ResponseEntity.ok(postDetailResponse);
  }

  @CurrentUserId
  @PatchMapping("/status/{id}")
  public ResponseEntity<PostDetailResponse> updatePostStatus(
      @PathVariable("id") long postId,
      @RequestBody @Valid PostUpdateStatusRequest postUpdateStatusRequest,
      Long memberId,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new NullParameterException(POST_STATUS_IS_NULL);
    }

    PostDetailResponse postDetailResponse = postService.updatePostStatus(postId,
        postUpdateStatusRequest, memberId);
    return ResponseEntity.ok(postDetailResponse);
  }

  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/search")
  public PostsSliceResponse search(@Valid PostSearchOptionRequest searchOption,
      @RequestParam("query") @NotBlank String query, @Valid PostSliceRequest postSliceRequest,
      Long memberId) {
    return postService.search(query, searchOption, memberId, postSliceRequest);
  }

  @CurrentUserId
  @PutMapping("/{id}")
  public ResponseEntity<PostDetailResponse> updatePost(@PathVariable("id") Long postId,
      @RequestBody PostUpdateRequest postUpdateRequest, Long memberId) {

    PostDetailResponse postDetailResponse = postService.updatePost(postUpdateRequest, memberId);
    return ResponseEntity.ok(postDetailResponse);
  }

  @CurrentUserId
  @PatchMapping("/likes/{id}")
  public ResponseEntity<Void> updateLikes(@PathVariable("id") Long postId,
      Long memberId) {

    postService.clickLikes(postId, memberId);

    return ResponseEntity.ok().build();

  }
}
