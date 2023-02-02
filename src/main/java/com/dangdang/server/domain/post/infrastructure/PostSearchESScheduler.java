package com.dangdang.server.domain.post.infrastructure;

import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostSearchESScheduler {

  private final PostService postService;

  public PostSearchESScheduler(PostService postService) {
    this.postService = postService;
  }

  @Scheduled(cron = "0 0 0/1 * * *")
  public void uploadToES() {
    try {
      postService.uploadToES();
    } catch(PostNotFoundException e) {
      log.info(e.getMessage());
    }
  }
}
