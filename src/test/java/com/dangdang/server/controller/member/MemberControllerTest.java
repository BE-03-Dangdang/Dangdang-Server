package com.dangdang.server.controller.member;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.application.MemberService;
import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
  private SmsMessageService smsMessageService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private RedisAuthCodeRepository redisAuthCodeRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TownRepository townRepository;

  @Autowired
  private RedisSmsRepository redisSmsRepository;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @DisplayName("회원가입이 성공적으로 되는 경우")
  @Transactional
  void signup_success() throws Exception {
    // given
    redisAuthCodeRepository.save(new RedisAuthCode("01012345678", true));
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("삼성동", "01012345678", null,
        "딸기");
    Town town = new Town("삼성동", new BigDecimal("11.13"), new BigDecimal("12.12"));
    townRepository.save(town);

    // when, then
    mockMvc.perform(post("/member/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberSignUpRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("회원가입 실패시 - redis 값이 없는 경우")
  @Transactional
  void signup_failByNotHavingKey() throws Exception {
    // given
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("삼성동", "딸기", "01011112222",
        null);
    // when, then
    mockMvc.perform(post("/member/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(memberSignUpRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print());

  }

  @Test
  @DisplayName("회원가입 실패시 - 동네이름이 DB에 없는 경우")
  @Transactional
  void signup_failByNotHavingTown() throws Exception {
    // given
    redisAuthCodeRepository.save(new RedisAuthCode("01012345678", true));
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("우주", "01012345678", null,
        "딸기");
    // when, then
    mockMvc.perform(post("/member/signup")
            .contentType(MediaType.APPLICATION_JSON)
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
    memberRepository.save(new Member("01012345678", null, "딸기"));

    // when, then
    mockMvc.perform(post("/member/loginCertify")
            .contentType(MediaType.APPLICATION_JSON)
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
    mockMvc.perform(post("/member/signupCertify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(phoneNumberCertifyRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }


}