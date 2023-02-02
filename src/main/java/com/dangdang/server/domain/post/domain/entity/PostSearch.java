package com.dangdang.server.domain.post.domain.entity;

import com.dangdang.server.domain.post.domain.Category;
import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.Getter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName= "post-search", createIndex = true)
@Mapping(mappingPath = "elastic/post-search-mapping.json")
@Setting(settingPath = "elastic/post-search-setting.json")
@Getter
public class PostSearch implements Persistable<String> {

  @Id
  private String id;

  private String title;

  private String content;

  private Category category;

  private Integer price;

  private Boolean sharing;

  private Long townId;

  private String townName;

  private String imageUrl;

  protected String status;

  @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime createdAt;

  private PostSearch(String id, String title, String content, Category category, Integer price,
      Boolean sharing, Long townId, String townName, String imageUrl, String status, LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.category = category;
    this.price = price;
    this.sharing = sharing;
    this.townId = townId;
    this.townName = townName;
    this.imageUrl = imageUrl;
    this.status = status;
    this.createdAt = createdAt;
  }

  public static PostSearch from(UpdatedPost post) {
    return new PostSearch(String.valueOf(post.getId()), post.getTitle(), post.getContent(), post.getCategory(),
        post.getPrice(), post.getSharing(), post.getTownId(), post.getTownName(), post.getImageUrl(), post.getStatus().name(),
        post.getCreatedAt());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return id == null || createdAt == null;
  }
}
