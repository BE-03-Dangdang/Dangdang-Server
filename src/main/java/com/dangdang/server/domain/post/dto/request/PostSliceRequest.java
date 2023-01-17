package com.dangdang.server.domain.post.dto.request;

import lombok.Getter;

@Getter
public class PostSliceRequest {

  private int page;
  private int size;

  private PostSliceRequest() {
  }
}
