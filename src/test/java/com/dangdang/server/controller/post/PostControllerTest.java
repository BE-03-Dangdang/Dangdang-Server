package com.dangdang.server.controller.post;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageRequestDto;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AutoConfigureRestDocs
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
  @DisplayName("사용자는 게시글 메인 페이지에서 페이지네이션을 적용한 전체 게시글을 조회할 수 있다.")
  public void findAll() throws Exception {
    // given
    PostSliceRequest postSliceRequest = new PostSliceRequest(0, 10);
    // when
    mockMvc.perform((MockMvcRequestBuilders.get("/posts?size=1&page=0")
            .contentType(MediaType.APPLICATION_JSON)
            .header("AccessToken", accessToken)
            .characterEncoding(StandardCharsets.UTF_8)
        ))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("PostController/findAll",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
//            requestFields(
//                fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지에 보여줄 사이즈"),
//                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호")
//            ),
            responseFields(
                fieldWithPath("postSliceResponses[].id").type(JsonFieldType.NUMBER)
                    .description("글 번호"),
                fieldWithPath("postSliceResponses[].title").type(JsonFieldType.STRING)
                    .description("글 제목"),
                fieldWithPath("postSliceResponses[].townName").type(JsonFieldType.STRING)
                    .description("글이 작성된 동네 이름"),
                fieldWithPath("postSliceResponses[].imageUrl").type(JsonFieldType.STRING)
                    .description("글 대표 이미지 링크"),
                fieldWithPath("postSliceResponses[].price").type(JsonFieldType.NUMBER)
                    .description("상품 가격"),
                fieldWithPath("postSliceResponses[].createdAt").type(JsonFieldType.STRING)
                    .description("글 생성일시"),
                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 글 존재 여부")
            )
        ));
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