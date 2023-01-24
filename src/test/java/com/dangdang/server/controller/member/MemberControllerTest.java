package com.dangdang.server.controller.member;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RedisAuthCodeRepository redisAuthCodeRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private RedisSmsRepository redisSmsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TownRepository townRepository;

  @Test
  @DisplayName("회원가입 실패시 - redis 값이 없는 경우")
  @Transactional
  void signup_failByNotHavingKey() throws Exception {
    // given
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("삼성동", "00000000000", null,
        "딸기");
    // when, then
    mockMvc.perform(post("/members/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(memberSignUpRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print());

  }

  @Test
  @DisplayName("회원가입이 성공적으로 되는 경우")
  @Transactional
  void signup_success() throws Exception {
    // given
    redisAuthCodeRepository.save(new RedisAuthCode("01012345670"));
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("삼성동", "01012345670", null,
        "딸기3");

    // when, then
    mockMvc.perform(post("/members/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(memberSignUpRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  @DisplayName("회원가입 실패시 - 동네이름이 DB에 없는 경우")
  void signup_failByNotHavingTown() throws Exception {
    // given
    redisAuthCodeRepository.save(new RedisAuthCode("01012345677"));
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("우주", "01012345677", null,
        "딸기1");
    // when, then
    mockMvc.perform(post("/members/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(memberSignUpRequest)))
        .andExpect(status().isNotFound())
        .andDo(print());

  }


  @Test
  @DisplayName("이미 계정이 있는 경우 - 로그인 시 번호 검증")
  @Transactional
  void loginPhoneNumberCertify_success() throws Exception {
    // given
    redisSmsRepository.save(new RedisSms("01012345678", "123456"));
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(
        "01012345678", "123456");
    memberRepository.save(new Member("01012345678", null, "딸기2"));

    // when, then
    mockMvc.perform(post("/members/login-certify")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(phoneNumberCertifyRequest)))
        .andExpect(status().isOk())
        .andDo(print());

  }

  @Test
  @DisplayName("시작하기 - 회원가입 시 번호 검증")
  @Transactional
  void signupPhoneNumberCertify_success() throws Exception {
    // given
    redisSmsRepository.save(new RedisSms("01012345678", "123456"));
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(
        "01012345678", "123456");

    // when, then
    mockMvc.perform(post("/members/signup-certify")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(phoneNumberCertifyRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }
//  @Test
//  void aa() {
//    townRepository.findAll().forEach(town -> System.out.println(town.getName()));
//  }
}