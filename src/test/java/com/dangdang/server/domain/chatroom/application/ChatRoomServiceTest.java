package com.dangdang.server.domain.chatroom.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomsResponse;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChatRoomServiceTest {

  @Autowired
  ChatRoomService chatRoomService;

  @Autowired
  ChatRoomRepository chatRoomRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TownRepository townRepository;

  @Autowired
  PostRepository postRepository;


  @Test
  @DisplayName("채팅방 생성이 성공적인 경우")
  void saveChatRoom_success() {
    Member buyer = new Member( "01012345678", "buyer");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("천호동동", null, null);
    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    Member savedBuyer = memberRepository.save(buyer);
    Member savedSeller = memberRepository.save(seller);
    townRepository.save(sellerTown);
    Post savedPost = postRepository.save(post);

    ChatRoomSaveRequest chatRoomSaveRequest = new ChatRoomSaveRequest(savedBuyer.getId(), savedSeller.getId(), savedPost.getId());

    ChatRoom savedChatRoom = chatRoomService.saveChatRoom(chatRoomSaveRequest);

    assertThat(savedChatRoom.getBuyer().getId()).isEqualTo(savedBuyer.getId());
    assertThat(savedChatRoom.getSeller().getId()).isEqualTo(savedSeller.getId());
    assertThat(savedChatRoom.getPost().getId()).isEqualTo(savedPost.getId());
  }

  @Test
  @DisplayName("채팅방 생성 실패 - post 를 찾을 수 없는 경우")
  void saveChatRoom_fail_byNotFoundPost() {
    Member buyer = new Member( "01012345678", "buyer");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("천호동동", null, null);
    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    Member savedBuyer = memberRepository.save(buyer);
    Member savedSeller = memberRepository.save(seller);
    townRepository.save(sellerTown);
    postRepository.save(post);

    ChatRoomSaveRequest chatRoomSaveRequest = new ChatRoomSaveRequest(savedBuyer.getId(), savedSeller.getId(), 0L);

    assertThatThrownBy(() -> chatRoomService.saveChatRoom(chatRoomSaveRequest))
        .isInstanceOf(PostNotFoundException.class);
  }

  @Test
  @DisplayName("채팅방 조회 성공")
  void findChatRooms_success() {
    // chatRoom 생성 필요
    // 1. member 2명 생성 필요
    // 2. post 생성 필요 -> town 생성 필요

    // 3. buyer 달리해서 ChatRoom 만들기

    Member buyer1 = new Member("01012345677", "buyer1");
    Member buyer2 = new Member("01012345678", "buyer2");
    Member buyer3 = new Member("01012345679", "buyer3");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("내동네", null, null);

    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    memberRepository.save(buyer1);
    memberRepository.save(buyer2);
    memberRepository.save(buyer3);
    Member savedSeller = memberRepository.save(seller);
    townRepository.save(sellerTown);
    postRepository.save(post);

    ChatRoom chatRoom1 = new ChatRoom(buyer1, seller, post);
    ChatRoom chatRoom2 = new ChatRoom(buyer2, seller, post);
    ChatRoom chatRoom3 = new ChatRoom(buyer3, seller, post);

    chatRoomRepository.save(chatRoom1);
    chatRoomRepository.save(chatRoom2);
    chatRoomRepository.save(chatRoom3);

    // when
    ChatRoomsResponse chatRoomsResponse = chatRoomService.findChatRooms(savedSeller.getId());

    // then
    assertThat(chatRoomsResponse.chatRoomResponseList().size()).isEqualTo(3);
  }
}