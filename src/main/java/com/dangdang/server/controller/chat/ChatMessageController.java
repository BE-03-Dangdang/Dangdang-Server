package com.dangdang.server.controller.chat;

import com.dangdang.server.domain.message.application.ChatMessageService;
import com.dangdang.server.domain.message.dto.ChatMessage;
import com.dangdang.server.domain.message.dto.response.ChatMessagesResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    // message DB 에 저장
    chatMessageService.saveChatMessage(chatMessage);
    template.convertAndSend("/subscribe/chat/room/" + chatMessage.chatRoomId(), chatMessage);
  }

  // 상세 채팅방 메세지 조회, 오래된 순서부터 조회
  @CurrentUserId
  @GetMapping("/chat-room/{room_id}")
  public ResponseEntity<ChatMessagesResponse> findChatMessages(@PathVariable("room_id") Long chatRoomId) {
    ChatMessagesResponse chatMessages = chatMessageService.findChatMessages(chatRoomId);
    return ResponseEntity.ok(chatMessages);
  }
}
