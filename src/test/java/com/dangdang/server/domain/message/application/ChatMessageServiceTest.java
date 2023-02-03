package com.dangdang.server.domain.message.application;

import static org.assertj.core.api.Assertions.*;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.message.domain.MessageRepository;
import com.dangdang.server.domain.message.domain.entity.Message;
import com.dangdang.server.domain.message.dto.ChatMessage;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class ChatMessageServiceTest {

  @Autowired
  ChatMessageService chatMessageService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TownRepository townRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  ChatRoomRepository chatRoomRepository;

  @Autowired
  MessageRepository messageRepository;


  @Test
  @DisplayName("채팅 메세지가 성공적으로 저장")
  @Transactional
  void saveChatMessage() {
    Member buyer = new Member("01012345677", "buyer");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("내동네", null, null);

    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    ChatRoom chatRoom = new ChatRoom(buyer, seller, post);

    Member savedBuyer = memberRepository.save(buyer);
    memberRepository.save(seller);
    townRepository.save(sellerTown);
    postRepository.save(post);
    ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

    ChatMessage chatMessage1 = new ChatMessage(savedChatRoom.getId(), savedBuyer.getNickname(), "안녕하세요 당근당근");
    ChatMessage chatMessage2 = new ChatMessage(savedChatRoom.getId(), savedBuyer.getNickname(), "에어팟 팔렸나요?");

    // when
    chatMessageService.saveChatMessage(chatMessage1);
    chatMessageService.saveChatMessage(chatMessage2);

    // then
    List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAt(savedChatRoom.getId());
    assertThat(messages.size()).isEqualTo(2);
    for (Message message : messages) {
      System.out.println(message.getMessage());
    }



  }
  @Test
  void print() {
    ChatRoom chatRoom = chatRoomRepository.findById(49L).get();
    List<Message> messageList = chatRoom.getMessageList();
    System.out.println(messageList.size());
    for (Message message : messageList) {
      System.out.println(message.getMessage());
    }
  }
}