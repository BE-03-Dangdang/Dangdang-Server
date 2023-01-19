package com.dangdang.server.domain.post.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
  private Integer view = 0;

  @Column(nullable = false)
  @ColumnDefault("false")
  private Boolean sharing = false;

  //연관관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "town_id", nullable = false)
  private Town town;

  protected Post() {
  }

  public Post(String title, String content, Category category, Integer price,
      String desiredPlaceName, BigDecimal desiredPlaceLongitude, BigDecimal desiredPlaceLatitude,
      Integer view, Boolean sharing, Member member, Town town) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.price = price;
    this.desiredPlaceName = desiredPlaceName;
    this.desiredPlaceLongitude = desiredPlaceLongitude;
    this.desiredPlaceLatitude = desiredPlaceLatitude;
    this.view = view;
    this.sharing = sharing;
    this.member = member;
    this.town = town;
  }
}
