package com.dangdang.server.controller.post;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.post.application.PostService;
import com.dangdang.server.domain.post.domain.Category;
import com.dangdang.server.domain.post.domain.PostRepository;
import com.dangdang.server.domain.post.dto.request.PostSaveRequest;
import com.dangdang.server.domain.post.dto.response.PostDetailResponse;
import com.dangdang.server.domain.postImage.domain.PostImageRepository;
import com.dangdang.server.domain.postImage.dto.PostImageRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTestForDetail {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  private SaveClassForViewUpdate saveClassForViewUpdate;

  @TestConfiguration
  static class testConfig {

    @Bean
    public SaveClassForViewUpdate innerClass() {
      return new SaveClassForViewUpdate();
    }
  }

  static class SaveClassForViewUpdate {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TownRepository townRepository;
    @Autowired
    MemberTownRepository memberTownRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    PostImageRepository postImageRepository;

    Member innerMember;
    Town innerTown;
    MemberTown innerMemberTown;
    PostSaveRequest postSaveRequest;
    PostDetailResponse postDetailResponse;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save() {

      Member newMember = new Member("01098764470", "testImgUrl", "테스트 멤버");
      innerMember = memberRepository.save(newMember);
      innerTown = townRepository.findByName("천호동").get();

      MemberTown newMemberTown = new MemberTown(innerMember, innerTown);
      innerMemberTown = memberTownRepository.save(newMemberTown);

      postSaveRequest = new PostSaveRequest("테스트 제목", "테스트 내용", Category.디지털기기,
          20000, "테스트 희망장소",
          BigDecimal.valueOf(127.0000), BigDecimal.valueOf(36.0000), false, "천호동",
          new PostImageRequest(new ArrayList<>()));

      postDetailResponse = postService.savePost(postSaveRequest, innerMember.getId());
    }

    public PostSaveRequest getPosSaveRequest() {
      return postSaveRequest;
    }

    public String getAccessToken() {
      return "Bearer " + jwtTokenProvider.createAccessToken(innerMember.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAfterTest() {
      memberRepository.deleteById(innerMember.getId());
      memberTownRepository.deleteById(innerMemberTown.getId());
      postRepository.deleteById(postDetailResponse.postId());
    }
  }

  @Test
  @DisplayName("게시글 상세 정보를 확인할 수 있다.")
  void findPostDetailTest() {
    try {
      saveClassForViewUpdate.save();
      String accessToken = saveClassForViewUpdate.getAccessToken();
      PostDetailResponse postDetailResponse = saveClassForViewUpdate.postDetailResponse;

      mockMvc.perform(
              RestDocumentationRequestBuilders.get("/posts/{id}", postDetailResponse.postId())
                  .header("AccessToken", accessToken))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(document("post/api/get/findById", preprocessResponse(prettyPrint()),
              requestHeaders(headerWithName("AccessToken").description("Access Token")),
              responseFields(
                  fieldWithPath("postResponse.id").type(JsonFieldType.NUMBER)
                      .description("게시글 식별자"),
                  fieldWithPath("postResponse.title").type(JsonFieldType.STRING)
                      .description("게시글 제목"),
                  fieldWithPath("postResponse.content").type(JsonFieldType.STRING)
                      .description("게시글 내용"),
                  fieldWithPath("postResponse.category").type(JsonFieldType.STRING)
                      .description("카테고리"),
                  fieldWithPath("postResponse.price").type(JsonFieldType.NUMBER).description("가격"),
                  fieldWithPath("postResponse.desiredPlaceName").type(JsonFieldType.STRING)
                      .description("거래희망 장소 이름").optional(),
                  fieldWithPath("postResponse.desiredPlaceLongitude").type(JsonFieldType.NUMBER)
                      .description("거래희망 장소 경도").optional(),
                  fieldWithPath("postResponse.desiredPlaceLatitude").type(JsonFieldType.NUMBER)
                      .description("거래희망 장소 위도").optional(),
                  fieldWithPath("postResponse.view").type(JsonFieldType.NUMBER).description("조회수"),
                  fieldWithPath("postResponse.sharing").type(JsonFieldType.BOOLEAN)
                      .description("나눔 여부"),
                  fieldWithPath("postResponse.townName").type(JsonFieldType.STRING)
                      .description("거래 기준 동네"),
                  fieldWithPath("postResponse.statusType").type(JsonFieldType.STRING)
                      .description("거래 상태"),
                  fieldWithPath("postResponse.likeCount").type(JsonFieldType.NUMBER)
                      .description("좋아요 개수"),
                  fieldWithPath("memberResponse.id").type(JsonFieldType.NUMBER)
                      .description("작성자 식별자"),
                  fieldWithPath("memberResponse.profileImgUrl").type(JsonFieldType.STRING)
                      .description("작성자 프로필 이미지 url").optional(),
                  fieldWithPath("memberResponse.nickName").type(JsonFieldType.STRING)
                      .description("작성자 닉네임"),
                  fieldWithPath("imageUrls").type(JsonFieldType.ARRAY)
                      .description("게시글 이미지 url 리스트")
                      .optional())));
    } catch (Exception exception) {
      log.info("########+ 예외발생" + exception.getMessage());
    } finally {
      saveClassForViewUpdate.deleteAfterTest();
    }
  }
}