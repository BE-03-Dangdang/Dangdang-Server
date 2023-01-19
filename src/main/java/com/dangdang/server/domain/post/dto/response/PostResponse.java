package com.dangdang.server.domain.post.dto.response;


import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PostResponse {

  private Long id;
  private String title;
  private String content;
  private Category category;
  private Integer price;
  private String desiredPlaceName;
  private BigDecimal desiredPlaceLongitude;
  private BigDecimal desiredPlaceLatitude;
  private Integer view;
  private Boolean sharing;
  private String townName;
  private StatusType statusType;

  private PostResponse(Long id, String title, String content, Category category, Integer price,
      String desiredPlaceName, BigDecimal desiredPlaceLongitude, BigDecimal desiredPlaceLatitude,
      Integer view, Boolean sharing, String townName, StatusType statusType) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.category = category;
    this.price = price;
    this.desiredPlaceName = desiredPlaceName;
    this.desiredPlaceLongitude = desiredPlaceLongitude;
    this.desiredPlaceLatitude = desiredPlaceLatitude;
    this.view = view;
    this.sharing = sharing;
    this.townName = townName;
    this.statusType = statusType;
  }

  public static PostResponse from(Post post) {
    return new PostResponse(
        post.getId(), post.getTitle(), post.getContent(), post.getCategory(),
        post.getPrice(), post.getDesiredPlaceName(), post.getDesiredPlaceLongitude(),
        post.getDesiredPlaceLatitude(), post.getView(), post.getSharing(),
        post.getTownName(), post.getStatus());
  }

}
