package com.dangdang.server.domain.post.dto.request;

import javax.validation.constraints.Min;
import lombok.Getter;

@Getter
public class PostSliceRequest {
  @Min(0)
  private int page;
  @Min(0)
  private int size;

  private PostSliceRequest() {
  }

  public PostSliceRequest(int page, int size) {
    this.page = page;
    this.size = size;
  }
}
