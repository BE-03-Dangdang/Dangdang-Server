package com.dangdang.server.domain.chatroom.application;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomResponse;
import com.dangdang.server.domain.chatroom.dto.response.ChatRoomsResponse;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.post.exception.PostNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;

  public ChatRoomService(ChatRoomRepository chatRoomRepository, MemberRepository memberRepository,
      PostRepository postRepository) {
    this.chatRoomRepository = chatRoomRepository;
    this.memberRepository = memberRepository;
    this.postRepository = postRepository;
  }

  @Transactional
  public ChatRoom saveChatRoom(ChatRoomSaveRequest chatRoomSaveRequest) {
    Member buyer = memberRepository.findById(chatRoomSaveRequest.buyerId())
        .orElseThrow(() -> new MemberNotFoundException(ExceptionCode.MEMBER_NOT_FOUND));

    Member seller = memberRepository.findById(chatRoomSaveRequest.sellerId())
        .orElseThrow(() -> new MemberNotFoundException(ExceptionCode.MEMBER_NOT_FOUND));

    Post post = postRepository.findById(chatRoomSaveRequest.postId())
        .orElseThrow(() -> new PostNotFoundException(ExceptionCode.POST_NOT_FOUND));

    ChatRoom chatRoom = new ChatRoom(buyer, seller, post);

    ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

    return savedChatRoom;
  }

  @Transactional
  public ChatRoomsResponse findChatRooms(Long memberId) {
    List<ChatRoom> chatRoomsByMemberId = chatRoomRepository.findByBuyerIdOrSellerId(memberId);

    List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();

    for (ChatRoom chatRoom : chatRoomsByMemberId) {
      Long chatRoomId = chatRoom.getId();
      String townName = chatRoom.getPost().getTownName();
      int size = chatRoom.getMessageList().size();
      String recentMessage = size != 0 ? chatRoom.getMessageList().get(size - 1).getMessage() : null;
      LocalDateTime recentMessageCreatedAt = size != 0 ? chatRoom.getMessageList().get(size - 1).getCreatedAt() : null;
      // 자기 자신이 buyer 일 때 -> seller
      ChatRoomResponse chatRoomResponse;
      if (chatRoom.getBuyer().getId() == memberId) {
        chatRoomResponse = new ChatRoomResponse(
            chatRoomId,
            chatRoom.getSeller().getProfileImgUrl(),
            chatRoom.getSeller().getNickname(),
            townName,
            recentMessage,
            recentMessageCreatedAt
        );
      }
      // 자기 자신이 seller 일 때 -> buyer
      else {
        chatRoomResponse = new ChatRoomResponse(
            chatRoomId,
            chatRoom.getBuyer().getProfileImgUrl(),
            chatRoom.getBuyer().getNickname(),
            townName,
            recentMessage,
            recentMessageCreatedAt
        );
      }
      chatRoomResponseList.add(chatRoomResponse);
    }

    return new ChatRoomsResponse(chatRoomResponseList);
  }

}
