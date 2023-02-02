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
  private int likeCount;

  private PostResponse(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.category = post.getCategory();
    this.price = post.getPrice();
    this.desiredPlaceName = post.getDesiredPlaceName();
    this.desiredPlaceLongitude = post.getDesiredPlaceLongitude();
    this.desiredPlaceLatitude = post.getDesiredPlaceLatitude();
    this.view = post.getView();
    this.sharing = post.getSharing();
    this.townName = post.getTownName();
    this.statusType = post.getStatus();
    this.likeCount = post.getLikeCount();
  }

  public static PostResponse from(Post post) {
    return new PostResponse(post);
  }

}
