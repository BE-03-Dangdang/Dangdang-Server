package com.dangdang.server.controller.post;

import static com.dangdang.server.global.exception.ExceptionCode.*;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostResponse;
import com.dangdang.server.domain.post.exception.NullParameterException;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PatchMapping("/{id}")
  public ResponseEntity<PostResponse> updatePostStatus(
      @PathVariable("id") long postId,
      @Valid PostUpdateStatusRequest postUpdateStatusRequest,
      Authentication authentication,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new NullParameterException(POST_STATUS_IS_NULL);
    }
    Long memberId = ((Member) authentication.getPrincipal()).getId();

    PostResponse postResponse = postService.updatePostStatus(postId, postUpdateStatusRequest,
        memberId);
    return ResponseEntity.ok(postResponse);
  }
}
