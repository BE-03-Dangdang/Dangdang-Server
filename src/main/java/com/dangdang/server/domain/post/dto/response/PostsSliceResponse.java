package com.dangdang.server.domain.post.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class PostsSliceResponse {

  private List<PostSliceResponse> postSliceResponses;
  private boolean hasNext;

  private PostsSliceResponse(List<PostSliceResponse> postSliceResponses, boolean hasNext) {
    this.postSliceResponses = postSliceResponses;
    this.hasNext = hasNext;
  }

  public static PostsSliceResponse of(List<PostSliceResponse> postSliceResponses, boolean hasNext) {
    return new PostsSliceResponse(postSliceResponses, hasNext);
  }
}
