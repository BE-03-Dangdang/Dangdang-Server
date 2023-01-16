package com.dangdang.server.domain.post.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.dto.request.PostFindAllRequest;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import jdk.jfr.Unsigned;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class Post extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "post_id")
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, length = 1000)
  private String content;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  @Column(columnDefinition = "INT UNSIGNED")
  private Integer price;

  @Column(length = 100)
  private String desiredPlaceName;

  @Column(precision = 18, scale = 10)
  private BigDecimal desiredPlaceLongitude;

  @Column(precision = 18, scale = 10)
  private BigDecimal desiredPlaceLatitude;

  @Column(nullable = false)
  @ColumnDefault("0")
  private Integer view;

  @Column(nullable = false)
  @ColumnDefault("false")
  private Boolean sharing = false;

  //연관관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "town_id")
  private Town town;

  protected Post() {
  }
}
