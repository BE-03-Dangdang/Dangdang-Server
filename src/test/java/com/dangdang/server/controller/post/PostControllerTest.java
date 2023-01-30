package com.dangdang.server.controller.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.request.PostSearchOptionRequest;
import com.dangdang.server.domain.post.dto.request.PostSliceRequest;
import com.dangdang.server.domain.post.dto.request.PostUpdateStatusRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.post.dto.response.PostsSliceResponse;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageRequestDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
  TownRepository townRepository;

  @Autowired
  MemberTownRepository memberTownRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  String accessToken;

  Long id;

  Member member;

  @BeforeEach
  void setUp() {
    member = new Member("010", "yb");
    memberRepository.save(member);
    accessToken = "Bearer " + jwtTokenProvider.createAccessToken(member.getId());
    Town town = townRepository.findByName("천호동").get();
    MemberTown memberTown = new MemberTown(member, town);
    memberTownRepository.save(memberTown);

    PostImageRequest postImageRequest = new PostImageRequest(
        List.of("http://s3.amazonaws.com/test1.png", "http://s3.amazonaws.com/test2.png"));
    PostSaveRequest postSaveRequest = new PostSaveRequest("지우개 팝니다.", "맛좋은 지우개 팔아요", Category.디지털기기,
        20000,
        null, null, null, false, "천호동",
        postImageRequest);
    PostDetailResponse postDetailResponse = postService.savePost(postSaveRequest, member.getId());
    id = postDetailResponse.getPostId();
  }


  @ParameterizedTest
  @ValueSource(strings = {"RESERVED", "SELLING", "COMPLETED"})
  @DisplayName("사용자는 판매중, 예약중, 판매완료 중 1개의 상태로 post의 상태를 변경할 수 있다.")
  void updatePostStatus(String status) throws Exception {

    mockMvc.perform(patch("/posts/" + id + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .header("AccessToken", accessToken)
            .content(objectMapper.writeValueAsString(new PostUpdateStatusRequest(
                StatusType.valueOf(status)))))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("사용자는 게시글을 검색 옵션을 사용해서 검색할 수 있다.")
  public void search() throws Exception {
    //given
    String query = "지우개";
    // when
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add("query", query);
    paramMap.add("rangeLevel", "4");
    paramMap.add("category", "생활가전,디지털기기");
    paramMap.add("isTransactionAvailableOnly", "true");
    paramMap.add("isTransactionAvailableOnly", "true");
    paramMap.add("minPrice", "1000");
    paramMap.add("page", "0");
    paramMap.add("size", "10");

    mockMvc.perform(get("/posts/search")
            .queryParams(paramMap)
            .header("AccessToken", accessToken))
        .andDo(print())
        .andExpect(status().isOk());

    //then

  }
}