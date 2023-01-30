package com.dangdang.server.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public record PostLikeRequest(
    @NotNull @JsonProperty("postId")
    Long postId,
    @NotNull @JsonProperty("memberId") Long memberId
) {

}
