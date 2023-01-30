package com.dangdang.server.controller.post;

import static com.dangdang.server.global.exception.ExceptionCode.POST_STATUS_IS_NULL;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.post.exception.NullParameterException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  // Town 완성 전까지 불가 로직.
  /*
  @GetMapping
  public ResponseEntity<PostsSliceResponse> findAll(
      @ModelAttribute @Valid PostSliceRequest postSliceRequest,
      Authentication authentication,
      BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      throw new MinParameterException(SLICE_PARAMETER_UNDER_ZERO);
    }
    Long memberId = ((Member) authentication.getPrincipal()).getId();
    // Town 완성 전까지 불가 로직.
//    postService.findPostsForSlice(postSliceRequest, )

  }
   */

  @PatchMapping("/{id}/status")
  public ResponseEntity<PostDetailResponse> updatePostStatus(@PathVariable("id") long postId,
      @RequestBody @Valid PostUpdateStatusRequest postUpdateStatusRequest,
      Authentication authentication, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new NullParameterException(POST_STATUS_IS_NULL);
    }
    Long memberId = ((Member) authentication.getPrincipal()).getId();

    PostDetailResponse postDetailResponse = postService.updatePostStatus(postId,
        postUpdateStatusRequest, memberId);
    return ResponseEntity.ok(postDetailResponse);
  }

  @GetMapping("/search")
  public ResponseEntity<PostsSliceResponse> search(@Valid PostSearchOptionRequest searchOption,
      @RequestParam("query") @NotBlank String query, @Valid PostSliceRequest postSliceRequest,
      Authentication authentication) {
    Long memberId = ((Member) authentication.getPrincipal()).getId();
    PostsSliceResponse searchResult = postService.search(query, searchOption, memberId,
        postSliceRequest);
    return ResponseEntity.ok(searchResult);
  }
}
