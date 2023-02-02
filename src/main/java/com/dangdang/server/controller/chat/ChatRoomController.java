package com.dangdang.server.controller.chat;

import com.dangdang.server.domain.chatroom.application.ChatRoomService;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomSaveResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  public ChatRoomController(ChatRoomService chatRoomService) {
    this.chatRoomService = chatRoomService;
  }

  @PostMapping("/chat-room")
  public ResponseEntity<ChatRoomSaveResponse> joinChatRoom(
      @RequestBody ChatRoomSaveRequest chatRoomSaveRequest) {
    ChatRoom chatRoom = chatRoomService.saveChatRoom(chatRoomSaveRequest);
    ChatRoomSaveResponse chatRoomSaveResponse = new ChatRoomSaveResponse(
        chatRoom.getBuyer().getId(),
        chatRoom.getSeller().getId(),
        chatRoom.getPost().getId()
    );
    return ResponseEntity.ok(chatRoomSaveResponse);
  }
}
