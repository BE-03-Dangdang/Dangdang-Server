package com.dangdang.server.controller;


import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.dto.request.PostLikeRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts")
@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
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
