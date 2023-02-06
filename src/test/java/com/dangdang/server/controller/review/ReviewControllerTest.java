package com.dangdang.server.controller.review;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.domain.entity.Post;
import com.dangdang.server.domain.review.dto.ReviewRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ReviewControllerTest {

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

  @Autowired
  TownRepository townRepository;

  String accessTokenWithReviewer;
  Member reviewer;
  Member reviewee;

  Post post;

  @Autowired
  private PostRepository postRepository;


  @BeforeEach
  void setUp() {
    Member memberForReviewer = new Member("01099994668", "kw1");
    Member memberForReviewee = new Member("01088884668", "kw2");
    reviewer = memberRepository.save(memberForReviewer);
    reviewee = memberRepository.save(memberForReviewee);
    Town town = townRepository.findByName("천호동").get();

    accessTokenWithReviewer = "Bearer " + jwtTokenProvider.createAccessToken(reviewer.getId());

    Post tmpPost = new Post("title1", "content1", Category.디지털기기, 10000, "desiredName1",
        null, null, 0, false, reviewee, town,
        null, StatusType.COMPLETED);

    post = postRepository.save(tmpPost);
  }

  @Test
  @DisplayName("Review를 작성할 수 있다.")
  void saveReviewTest() throws Exception {
    ReviewRequest reviewRequest = new ReviewRequest(post.getId(),
        reviewee.getId(), "preference1",
        "nicePoint", "content1");
    mockMvc.perform((MockMvcRequestBuilders.post("/reviews"))
            .contentType(MediaType.APPLICATION_JSON)
            .header("AccessToken", accessTokenWithReviewer)
            .content(objectMapper.writeValueAsString(reviewRequest)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andDo(MockMvcResultHandlers.print())
        .andDo(document("reviews/api/post/save",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName("AccessToken").description("Access Token")
            ),
            requestFields(
                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                fieldWithPath("revieweeId").type(JsonFieldType.NUMBER).description("리뷰이 식별자"),
                fieldWithPath("preference").type(JsonFieldType.STRING).description("선호도"),
                fieldWithPath("nicePoint").type(JsonFieldType.STRING).description("거래평점(온도)"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰내용")),
            responseFields(
                fieldWithPath("reviewId").type(JsonFieldType.NUMBER).description("리뷰 식별자"),
                fieldWithPath("postTitle").type(JsonFieldType.STRING).description("거래 게시글 제목"),
                fieldWithPath("townName").type(JsonFieldType.STRING).description("거래 장소"),
                fieldWithPath("reviewer.id").type(JsonFieldType.NUMBER)
                    .description("리뷰어 식별자"),
                fieldWithPath("reviewer.profileImgUrl").type(JsonFieldType.STRING).optional()
                    .description("리뷰어 프로필이미지"),
                fieldWithPath("reviewer.nickName").type(JsonFieldType.STRING)
                    .description("리뷰어 닉네임"),
                fieldWithPath("reviewee.id").type(JsonFieldType.NUMBER)
                    .description("리뷰이 식별자"),
                fieldWithPath("reviewee.profileImgUrl").type(JsonFieldType.STRING).optional()
                    .description("리뷰이 프로필이미지"),
                fieldWithPath("reviewee.nickName").type(JsonFieldType.STRING)
                    .description("리뷰이 닉네임"),
                fieldWithPath("preference").type(JsonFieldType.STRING).description("선호도"),
                fieldWithPath("nicePoint").type(JsonFieldType.STRING).description("거래평점(온도)"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰내용")
            )));
  }
}