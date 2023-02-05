package com.dangdang.server.domain.message.domain.entity;

import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.common.BaseEntity;
import com.dangdang.server.domain.message.dto.ChatMessage;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Message extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;

  @JoinColumn(name = "sender_nickname")
  private String senderNickName;

  @Lob
  @Column(name = "message")
  private String message;

  protected Message() {

  }

  public Message(ChatRoom chatRoom, String senderNickName, String message) {
    this.chatRoom = chatRoom;
    this.senderNickName = senderNickName;
    this.message = message;
  }

  public static ChatMessage toChatMessage(Message message) {
    return new ChatMessage(
        message.getChatRoom().getId(),
        message.getSenderNickName(),
        message.getMessage());
  }
}
