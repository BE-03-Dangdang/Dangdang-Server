package com.dangdang.server.controller.chat;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.message.domain.MessageRepository;
import com.dangdang.server.domain.message.domain.entity.Message;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class ChatRoomControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

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

  @Autowired
  JwtTokenProvider jwtTokenProvider;


  @Test
  @DisplayName("채팅 방 생성 성공")
  @Transactional
  void createChatRoom_success() throws Exception {
    Member buyer = new Member("01089796288", "buyer");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("천호동동", null, null);
    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    Member savedBuyer = memberRepository.save(buyer);
    Member savedSeller = memberRepository.save(seller);
    townRepository.save(sellerTown);
    Post savedPost = postRepository.save(post);

    ChatRoomSaveRequest chatRoomSaveRequest = new ChatRoomSaveRequest(
        savedBuyer.getId(), savedSeller.getId(), savedPost.getId());

    mockMvc.perform(post("/chat-room")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(chatRoomSaveRequest)))
        .andExpect(status().isOk())
        .andDo(
            document("api/v1/post/chat-room",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("buyerId").description("구매자 아이디"),
                    fieldWithPath("sellerId").description("판매자 아이디"),
                    fieldWithPath("postId").description("게시글 아이디")
                ),
                responseFields(
                    fieldWithPath("buyerId").description("구매자 아이디"),
                    fieldWithPath("sellerId").description("판매자 아이디"),
                    fieldWithPath("postId").description("게시글 아이디")
                )
            ));
  }

  @Test
  @DisplayName("채팅 방 생성 실패 - post 찾을 수 없음")
  @Transactional
  void createChatRoom_fail_byNotFoundPost() throws Exception {
    Member buyer = new Member("01089796288", "buyer");
    Member seller = new Member("01087654321", "seller");
    Town sellerTown = new Town("천호동동", null, null);
    Post post = new Post("에어팟", "에어팟 팝니다", Category.디지털기기, null,
        null, null, null,
        0, false, seller, sellerTown, null, null);

    Member savedBuyer = memberRepository.save(buyer);
    Member savedSeller = memberRepository.save(seller);
    townRepository.save(sellerTown);
    postRepository.save(post);

    ChatRoomSaveRequest chatRoomSaveRequest = new ChatRoomSaveRequest(
        savedBuyer.getId(), savedSeller.getId(), 0L);

    mockMvc.perform(post("/chat-room")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(chatRoomSaveRequest)))
        .andExpect(status().isNotFound())
        .andDo(print());

  }

  @Test
  @DisplayName("채팅 방 조회 성공")
  @Transactional
  void findChatRooms_success() throws Exception {
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


    Message message1 = new Message(chatRoom1, buyer1.getNickname(), "안녕하세요! 맥북 air 있나요?");
    Message message2 = new Message(chatRoom2, buyer2.getNickname(), "안녕하세요! 맥북 pro 있나요?");
    Message message3 = new Message(chatRoom3, buyer3.getNickname(), "안녕하세요! 아이폰 있나요?");

    chatRoom1.getMessageList().add(message1);
    chatRoom2.getMessageList().add(message2);
    chatRoom3.getMessageList().add(message3);


    chatRoomRepository.save(chatRoom1);
    chatRoomRepository.save(chatRoom2);
    chatRoomRepository.save(chatRoom3);

    String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(savedSeller.getId());

    mockMvc.perform(get("/chat-room")
            .header("AccessToken",accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("api/v1/get/chat-room",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("Access Token")
                ),
                responseFields(
                    fieldWithPath("chatRoomResponseList[]").type(JsonFieldType.ARRAY).description("채팅방 조회 목록 리스트"),
                    fieldWithPath("chatRoomResponseList[].roomId").description("채팅방 아이디"),
                    fieldWithPath("chatRoomResponseList[].profileImage").description("대화 상대방 프로필 이미지"),
                    fieldWithPath("chatRoomResponseList[].nickName").description("대화 상대방 닉네임"),
                    fieldWithPath("chatRoomResponseList[].townName").description("판매글 동네이름"),
                    fieldWithPath("chatRoomResponseList[].recentMessage").description("최근 메세지"),
                    fieldWithPath("chatRoomResponseList[].recentMessageCreatedAt").description("최근 메세지 시각")
                )
            ));
  }
}