package com.dangdang.server.domain.post.dto.request;

import static com.dangdang.server.global.util.S3ConstantMessage.DEFAULT_IMAGE_LINK;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record PostSaveRequest(
    @JsonProperty @NotBlank @Max(value = 255) String title,
    @JsonProperty @NotBlank @Max(value = 1000) String content,
    @JsonProperty @NotBlank Category category,
    @JsonProperty @Min(value = 0) Integer price,
    @JsonProperty @Size(max = 100) String desiredPlaceName,
    @JsonProperty BigDecimal desiredPlaceLongitude,
    @JsonProperty BigDecimal desiredPlaceLatitude,
    @JsonProperty @NotNull Boolean sharing,
    @JsonProperty @NotBlank @Size(max = 10) String townName,
    @JsonProperty @Size(max = 10) PostImageRequest postImageRequest
) {

  private static final int VIEW_INIT_VALUE = 0;

  public static Post toPost(PostSaveRequest postSaveRequest, Member loginMember, Town town) {
    return new Post(postSaveRequest.title, postSaveRequest.content, postSaveRequest.category,
        postSaveRequest.price, postSaveRequest.desiredPlaceName,
        postSaveRequest.desiredPlaceLongitude, postSaveRequest.desiredPlaceLatitude,
        VIEW_INIT_VALUE, postSaveRequest.sharing, loginMember, town,
        postSaveRequest.postImageRequest.urls().size() == 0 ? DEFAULT_IMAGE_LINK
            : postSaveRequest.postImageRequest.urls().get(0), StatusType.SELLING);
  }
}
