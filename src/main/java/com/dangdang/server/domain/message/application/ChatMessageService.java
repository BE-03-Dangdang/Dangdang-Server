package com.dangdang.server.domain.message.application;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.exception.ChatRoomNotFoundException;
import com.dangdang.server.domain.message.domain.MessageRepository;
import com.dangdang.server.domain.message.domain.entity.Message;
import com.dangdang.server.domain.message.dto.ChatMessage;
import com.dangdang.server.domain.message.dto.response.ChatMessagesResponse;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {

  private final MessageRepository messageRepository;
  private final ChatRoomRepository chatRoomRepository;

  public ChatMessageService(MessageRepository messageRepository,
      ChatRoomRepository chatRoomRepository) {
    this.messageRepository = messageRepository;
    this.chatRoomRepository = chatRoomRepository;
  }

  @Transactional
  public void saveChatMessage(ChatMessage chatMessage) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.chatRoomId())
        .orElseThrow(() -> new ChatRoomNotFoundException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
    Message message = new Message(chatRoom, chatMessage.senderNickName(), chatMessage.message());
    messageRepository.save(message);
  }

  @Transactional
  public ChatMessagesResponse findChatMessages(Long chatRoomId) {
    List<Message> chatMessages = messageRepository.findByChatRoomIdOrderByCreatedAt(
        chatRoomId);
    List<ChatMessage> chatMessagesResponse = chatMessages
        .stream()
        .map(Message::toChatMessage)
        .collect(Collectors.toList());
    return new ChatMessagesResponse(chatMessagesResponse);
  }
}
