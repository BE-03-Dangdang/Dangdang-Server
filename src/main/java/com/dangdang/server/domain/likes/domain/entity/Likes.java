package com.dangdang.server.domain.likes.domain.entity;

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

@Entity
public class Likes extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "likes_id")
  private Long likesId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  protected Likes() {
  }

  public Likes(Post post, Member member) {
    this.post = post;
    this.member = member;
  }

  public void addLikes() {
    this.post.addLikes(this);
    this.member.addLikes(this);
  }

}
