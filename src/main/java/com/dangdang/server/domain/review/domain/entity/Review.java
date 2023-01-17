package com.dangdang.server.domain.review.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.entity.Post;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_id")
  private Member reviewerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewee_id")
  private Member revieweeId;

  @Column(nullable = false, length = 100)
  private String preference;

  @Column(nullable = false, name = "nice_point", length = 100)
  private String nicePoint;

  @Column(length = 1000)
  private String content;

  protected Review() {
  }

}
