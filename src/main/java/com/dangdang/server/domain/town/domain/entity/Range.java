package com.dangdang.server.domain.town.domain.entity;

import java.util.List;
import javax.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("range")
@ToString
@Getter
public class Range {

  @Id
  private ObjectId id;
  private String name;
  private String level;
  private List<Long> ids;
  private List<String> names;

  public Range(String name, String level, List<Long> ids, List<String> names) {
    this.name = name;
    this.level = level;
    this.ids = ids;
    this.names = names;
  }
}
