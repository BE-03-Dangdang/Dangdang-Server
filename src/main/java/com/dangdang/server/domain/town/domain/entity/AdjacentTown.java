package com.dangdang.server.domain.town.domain.entity;


import javax.persistence.Id;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "adjacentTown")
@Getter
public class AdjacentTown {

  @Id
  private ObjectId id;
  private String name;
  private Range[] ranges;


  public AdjacentTown(String name, Range[] ranges) {
    this.name = name;
    this.ranges = ranges;
  }
}
