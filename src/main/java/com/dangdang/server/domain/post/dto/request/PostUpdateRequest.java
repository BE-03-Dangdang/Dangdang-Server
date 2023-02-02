package com.dangdang.server.domain.post.dto.request;

import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PostUpdateRequest(
    @NotNull @JsonProperty() Long id,
    @NotBlank @JsonProperty() String title,
    @NotBlank @JsonProperty() String content,
    @NotBlank @JsonProperty() Category category,
    @JsonProperty() Integer price,
    @JsonProperty() String desiredPlaceName,
    @JsonProperty() BigDecimal desiredPlaceLongitude,
    @JsonProperty() BigDecimal desiredPlaceLatitude,
    @NotNull @JsonProperty() Boolean sharing,
    @JsonProperty() PostImageRequest postImageRequest) {

  public static Post to(PostUpdateRequest postUpdateRequest) {
    return new Post(postUpdateRequest.title(), postUpdateRequest.content(),
        postUpdateRequest.category(),
        postUpdateRequest.price(), postUpdateRequest.desiredPlaceName(),
        postUpdateRequest.desiredPlaceLongitude(), postUpdateRequest.desiredPlaceLatitude(),
        postUpdateRequest.sharing(), postUpdateRequest.postImageRequest().representativeImage());
  }
}