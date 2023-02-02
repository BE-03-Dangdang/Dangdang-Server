package com.dangdang.server.controller.chat;

import com.dangdang.server.domain.message.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {

  private final SimpMessagingTemplate template;

  public ChatMessageController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/message")
  public void message(ChatMessage chatMessage) {
    template.convertAndSend("/subscribe/chat/room/" + chatMessage.chatRoomId(), chatMessage);
  }
}
