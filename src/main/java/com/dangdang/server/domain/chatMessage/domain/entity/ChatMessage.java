package com.dangdang.server.domain.chatMessage.domain.entity;

import com.dangdang.server.domain.common.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class ChatMessage extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "chat_message_id")
  private Long id;

  protected ChatMessage() {
  }

}
