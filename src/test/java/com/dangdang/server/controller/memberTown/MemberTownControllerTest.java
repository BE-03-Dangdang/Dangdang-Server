package com.dangdang.server.controller.memberTown;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.entity.TownRepository;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class MemberTownControllerTest {

  @Autowired
  WebApplicationContext context;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  TownRepository townRepository;

  @Autowired
  MemberTownRepository memberTownRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  Member member;
  String accessToken;


  @BeforeEach
  void setup() {
    member = new Member("01012345678", null, "Albatross");
    Member save = memberRepository.save(member);
    accessToken = "Bearer " + jwtTokenProvider.createAccessToken(save.getId());
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(print())
        .apply(springSecurity())
        .build();
  }

  @AfterEach
  void clear() {
    memberRepository.deleteById(member.getId());
  }

  @Test
  @DisplayName("멤버 타운 생성 성공")
  @Transactional
  void createMemberTown() throws Exception {
    // given
    // member-town 1개 생성되어 있어야 (가입할 때 되어 있는 부분)
    Town existingTown = townRepository.findByName("삼성2동").get();
    MemberTown existingMemberTown = new MemberTown(member, existingTown);
    memberTownRepository.save(existingMemberTown);

    MemberTownRequest memberTownRequest = new MemberTownRequest("삼성1동");

    mockMvc.perform(post("/member-town")
            .header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberTownRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("멤버 타운 삭제 성공")
  @Transactional
  void deleteMemberTown() throws Exception {
    // given
    // member-town 2개 있어야 함 (existingMemberTown1이 Inactive 가 된다)
    Town existingTown1 = townRepository.findByName("삼성1동").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("삼성2동").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    MemberTownRequest memberTownRequest = new MemberTownRequest("삼성2동");
    System.out.println(memberTownRequest.townName());

    mockMvc.perform(delete("/member-town")
            .header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberTownRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("멤버 타운 활성화 변경 성공")
  @Transactional
  void changeActiveMemberTown() throws Exception {
    // given
    // member-town 2개 있어야 함 (삼성1동이 Inactive 가 된다)
    Town existingTown1 = townRepository.findByName("삼성1동").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("삼성2동").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    // 삼성 1동으로 Active 변경 요청
    MemberTownRequest memberTownRequest = new MemberTownRequest("삼성1동");

    mockMvc.perform(put("/member-town/active")
            .header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberTownRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("멤버 타운 범위 변경 성공")
  @Transactional
  void changeMemberTownRange() throws Exception {
    // given
    Town existingTown1 = townRepository.findByName("삼성1동").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("삼성1동", 3);

    mockMvc.perform(put("/member-town/range")
            .header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberTownRangeRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }
}