package com.dangdang.server.domain.post.domain;

import static com.dangdang.server.global.exception.ExceptionCode.CATEGORY_NOT_FOUND;

import com.dangdang.server.domain.post.exception.CategoryNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

public enum Category {

  디지털기기("DigitalDevices"), 생활가전("HouseholdAppliances"), 유아동("Child"), 유아도서("ChildBook");

  private final String name;

  Category(String name) {
    this.name = name;
  }

  @JsonCreator
  public static Category selectCategory(String name) {
    return Stream.of(values())
        .filter(category -> category.name.equals(name))
        .findFirst()
        .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
