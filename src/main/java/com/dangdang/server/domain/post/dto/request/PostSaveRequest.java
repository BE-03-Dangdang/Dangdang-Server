package com.dangdang.server.domain.post.dto.request;

import static com.dangdang.server.global.util.S3ConstantMessage.DEFAULT_IMAGE_LINK;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostSaveRequest {

  private static final int VIEW_INIT_VALUE = 0;

  @NotBlank
  @Max(value = 255)
  private String title;

  @NotBlank
  @Max(value = 1000)
  private String content;

  @NotBlank
  private Category category;

  @Min(value = 0)
  private Integer price;

  @Size(max = 100)
  private String desiredPlaceName;

  private BigDecimal desiredPlaceLongitude;

  private BigDecimal desiredPlaceLatitude;

  @NotNull
  private Boolean sharing;

  @NotBlank
  @Size(max = 10)
  private String townName;

  @Size(max = 10)
  private PostImageRequest postImageRequest;

  @JsonCreator
  public PostSaveRequest(String title, String content, Category category, Integer price,
      String desiredPlaceName, BigDecimal desiredPlaceLongitude, BigDecimal desiredPlaceLatitude,
      Boolean sharing, String townName, PostImageRequest postImageRequest) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.price = price;
    this.desiredPlaceName = desiredPlaceName;
    this.desiredPlaceLongitude = desiredPlaceLongitude;
    this.desiredPlaceLatitude = desiredPlaceLatitude;
    this.sharing = sharing;
    this.townName = townName;
    this.postImageRequest = postImageRequest;
  }

  public static Post toPost(PostSaveRequest postSaveRequest, Member loginMember, Town town) {
    return new Post(postSaveRequest.title, postSaveRequest.content, postSaveRequest.category,
        postSaveRequest.price, postSaveRequest.desiredPlaceName,
        postSaveRequest.getDesiredPlaceLongitude(), postSaveRequest.desiredPlaceLatitude,
        VIEW_INIT_VALUE, postSaveRequest.sharing, loginMember, town,
        postSaveRequest.postImageRequest.urls().size() == 0 ? DEFAULT_IMAGE_LINK
            : postSaveRequest.postImageRequest.urls().get(0), StatusType.SELLING);
  }
}
