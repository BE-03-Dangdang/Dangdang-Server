package com.dangdang.server.controller.chat;

import com.dangdang.server.domain.message.application.ChatMessageService;
import com.dangdang.server.domain.message.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMessageController {

  private final SimpMessagingTemplate template;
  private final ChatMessageService chatMessageService;

  public ChatMessageController(SimpMessagingTemplate template,
      ChatMessageService chatMessageService) {
    this.template = template;
    this.chatMessageService = chatMessageService;
  }

  @MessageMapping("/message")
  public void message(ChatMessage chatMessage) {
    // TODO message DB에 저장
    template.convertAndSend("/subscribe/chat/room/" + chatMessage.chatRoomId(), chatMessage);
  }
}
