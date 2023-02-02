package com.dangdang.server.domain.post.dto.response;


import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import java.math.BigDecimal;

public record PostResponse(
    Long id,
    String title,
    String content,
    Category category,
    Integer price,
    String desiredPlaceName,
    BigDecimal desiredPlaceLongitude,
    BigDecimal desiredPlaceLatitude,
    Integer view,
    Boolean sharing,
    String townName,
    StatusType statusType
) {

  public static PostResponse from(Post post) {
    return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getCategory(),
        post.getPrice(), post.getDesiredPlaceName(), post.getDesiredPlaceLongitude(),
        post.getDesiredPlaceLatitude(), post.getView(), post.getSharing(), post.getTownName(),
        post.getStatus());
  }
}
