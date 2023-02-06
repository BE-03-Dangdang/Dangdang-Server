package com.dangdang.server.controller.chat;

import com.dangdang.server.domain.chatroom.application.ChatRoomService;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomSaveResponse;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomsResponse;
import com.dangdang.server.domain.message.application.ChatMessageService;
import com.dangdang.server.global.aop.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @CurrentUserId
  @GetMapping("/chat-room")
  public ResponseEntity<ChatRoomsResponse> findChatRooms(Long memberId) {
    ChatRoomsResponse chatRoomsResponse = chatRoomService.findChatRooms(memberId);
    return ResponseEntity.ok(chatRoomsResponse);
  }
}
