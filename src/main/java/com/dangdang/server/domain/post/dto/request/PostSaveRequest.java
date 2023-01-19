package com.dangdang.server.domain.post.dto.request;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PostSaveRequest {

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

  @JsonCreator
  public PostSaveRequest(String title, String content, Category category, Integer price,
      String desiredPlaceName, BigDecimal desiredPlaceLongitude, BigDecimal desiredPlaceLatitude,
      Boolean sharing, String townName) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.price = price;
    this.desiredPlaceName = desiredPlaceName;
    this.desiredPlaceLongitude = desiredPlaceLongitude;
    this.desiredPlaceLatitude = desiredPlaceLatitude;
    this.view = 0;
    this.sharing = sharing;
    this.townName = townName;
  }

  public static Post toPost(PostSaveRequest postSaveRequest, Member loginMember, Town town) {
    return new Post(postSaveRequest.title, postSaveRequest.content, postSaveRequest.category,
        postSaveRequest.price, postSaveRequest.desiredPlaceName,
        postSaveRequest.getDesiredPlaceLongitude(), postSaveRequest.desiredPlaceLatitude,
        postSaveRequest.view, postSaveRequest.sharing, loginMember, town, StatusType.SELLING);
  }
}
