package com.dangdang.server.controller.chat;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.chatroom.domain.ChatRoomRepository;
import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
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
class ChatMessageControllerTest {

  @Autowired
  MockMvc mockMvc;

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
  @DisplayName("채팅 방 메세지 조회 성공")
  @Transactional
  void findChatMessages_success() throws Exception {
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

    Message message1 = new Message(chatRoom, savedBuyer.getNickname(), "안녕하세요 당근당근");
    Message message2 = new Message(chatRoom, savedBuyer.getNickname(), "에어팟 파나요??");

    messageRepository.save(message1);
    messageRepository.save(message2);

    String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(savedBuyer.getId());

    // when
    mockMvc.perform(get("/chat-room/" + savedChatRoom.getId())
            .header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(
            document("api/v1/get/chat-room/message",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("Access Token")
                ),
                responseFields(
                    fieldWithPath("chatMessages[]").type(JsonFieldType.ARRAY).description("채팅방 메세지들"),
                    fieldWithPath("chatMessages[].chatRoomId").description("채팅방 아이디"),
                    fieldWithPath("chatMessages[].senderNickName").description("메세지 보낸 사람"),
                    fieldWithPath("chatMessages[].message").description("메세지")
                )
            ));
  }
}