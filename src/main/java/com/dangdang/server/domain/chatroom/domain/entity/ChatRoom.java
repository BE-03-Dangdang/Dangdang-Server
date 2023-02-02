package com.dangdang.server.domain.chatroom.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.message.domain.entity.Message;
import com.dangdang.server.domain.post.domain.entity.Post;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;

@Getter
@Entity
public class ChatRoom extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "buyer_id")
  private Member buyer;

  @ManyToOne
  @JoinColumn(name = "seller_id")
  private Member seller;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
  List<Message> messageList = new ArrayList<>();

  protected ChatRoom() {
  }

  public ChatRoom(Member buyer, Member seller, Post post) {
    this.buyer = buyer;
    this.seller = seller;
    this.post = post;
  }
}
