package com.dangdang.server.controller.chat;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.chatroom.dto.request.ChatRoomSaveRequest;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
            document("api/v1/chat-room",
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
}