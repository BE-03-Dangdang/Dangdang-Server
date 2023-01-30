package com.dangdang.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PostControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  PostService postService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  String accessToken;

  Long id;

  @BeforeEach
  void setUp() {
    Member member = new Member("01098765467", "yb");
    memberRepository.save(member);
    accessToken = "Bearer " + jwtTokenProvider.createAccessToken(member.getId());
    PostImageRequest postImageRequest = new PostImageRequest(
        List.of("http://s3.amazonaws.com/test1.png", "http://s3.amazonaws.com/test2.png"));
    PostSaveRequest postSaveRequest = new PostSaveRequest("테스트 제목", "테스트 내용", Category.디지털기기, 20000,
        null, null, null, false, "천호동",
        postImageRequest);
    PostDetailResponse postDetailResponse = postService.savePost(postSaveRequest, member);
    id = postDetailResponse.getPostId();
  }


  @ParameterizedTest
  @ValueSource(strings = {"RESERVED", "SELLING", "COMPLETED"})
  @DisplayName("사용자는 판매중, 예약중, 판매완료 중 1개의 상태로 post의 상태를 변경할 수 있다.")
  void updatePostStatus(String status) throws Exception {

    mockMvc.perform(patch("/posts/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .header("AccessToken", accessToken)
            .content(objectMapper.writeValueAsString(new PostUpdateStatusRequest(
                StatusType.valueOf(status)))))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
  }
}