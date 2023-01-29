package com.dangdang.server.controller.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.RedisSendSmsRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.dto.request.MemberRefreshRequest;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import com.dangdang.server.global.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
@Transactional
class MemberRestDocsTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  SmsMessageService smsMessageService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  RedisAuthCodeRepository redisAuthCodeRepository;
  @Autowired
  JwtTokenProvider jwtTokenProvider;
  @Autowired
  RedisSendSmsRepository redisSendSmsRepository;

  @AfterEach
  void setup() {
    redisSendSmsRepository.deleteAll();
  }

  @Test
  @DisplayName("회원 가입 시 핸드폰 번호와 인증 번호로 요청함, 회원 가입이 되어 있지 않다면 Http 200 상태코드가 응답됨")
  void signupCertifyTest() throws Exception {
    //given
    //인증 문자 발송
    String phoneNumber = "01012345678";
    SmsRequest smsRequest = new SmsRequest(phoneNumber);
    String authCode = smsMessageService.sendMessage(smsRequest);

    //인증 요청
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    String json = objectMapper.writeValueAsString(phoneNumberCertifyRequest);
    //when
    //then
    mockMvc.perform(
            post("/members/signup-certify")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(result -> jsonPath("accessToken").value(null))
        .andExpect(result -> jsonPath("isCertified").value(true))
        .andDo(
            document(
                "MemberController/signupCertify",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                        .description("인증 코드를 받은 번호"),
                    fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증 코드")
                ),
                responseFields(
                    fieldWithPath("accessToken").type(JsonFieldType.NULL)
                        .description("accessToken"),
                    fieldWithPath("refreshToken").type(JsonFieldType.NULL).description("리플레쉬 토큰"),
                    fieldWithPath("isCertified").type(JsonFieldType.BOOLEAN).description("인증 여부")
                )
            )
        );
  }

  @Test
  @DisplayName("로그인 시 핸드폰 번호와 인증 코드로 요청해야 하며, 성공 시 http 200 status code와 accessToken 이 발급됨")
  void loginCertifyTest() throws Exception {
    //회원 가입된 정보 생성
    long id = 1L;
    String phoneNuber = "01012345678";
    String nickname = "cloudwi";
    Member member = new Member(1L, phoneNuber, nickname);
    memberRepository.save(member);

    //인증 문자 발송
    SmsRequest smsRequest = new SmsRequest(phoneNuber);
    String authCode = smsMessageService.sendMessage(smsRequest);

    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNuber,
        authCode);

    String json = objectMapper.writeValueAsString(phoneNumberCertifyRequest);

    mockMvc.perform(
            post("/members/login-certify")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "MemberController/loginCertify",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                        .description("인증 코드를 받은 번호"),
                    fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증 코드")
                ),
                responseFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("accessToken"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리플레쉬 토큰"),
                    fieldWithPath("isCertified").type(JsonFieldType.BOOLEAN).description("인증 여부")
                )
            )
        );
  }

  @Test
  @DisplayName("/api/v1/signup -> 닉네임과 프로필 이미지 핸드폰 번호 지역 이름으로 요청, 성공 시 http 200 status code와 accessToken으로 응답")
  void signUpTest() throws Exception {
    //회원 가입된 정보 생성
    long id = 1L;
    String phoneNuber = "01012345678";

    //휴대폰 인증 완료 등록
    redisAuthCodeRepository.save(new RedisAuthCode(phoneNuber));

    //requestDto 생성
    String townName = "역삼동";
    String nickname = "cloudwi";

    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(townName, phoneNuber,
        nickname);

    String json = objectMapper.writeValueAsString(memberSignUpRequest);

    mockMvc.perform(
            post("/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "MemberController/signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("townName").type(JsonFieldType.STRING).description("첫 등록 지역 이름"),
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING)
                        .description("인증 코드를 받은 번호"),
                    fieldWithPath("profileImgUrl").type(JsonFieldType.NULL)
                        .description("프로필 이미지는 Optional"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("인증 코드")
                ),
                responseFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("accessToken"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리플레쉬 토큰"),
                    fieldWithPath("isCertified").type(JsonFieldType.BOOLEAN).description("인증 여부")
                )
            )
        );
  }

  @Test
  @DisplayName("/api/v1/refresh -> 회원은 리플레쉬 토큰으로 2개의 토큰을 재 발급 받을 수 있다.")
  void refresh() throws Exception {
    //회원 가입된 정보 생성
    Member member = new Member("01012345678", "cloudwi");
    Member save = memberRepository.save(member);

    String refreshToken = jwtTokenProvider.createRefreshToken(save.getId());
    member.setRefreshToken(refreshToken);

    MemberRefreshRequest memberRefreshRequest = new MemberRefreshRequest(refreshToken);

    String json = objectMapper.writeValueAsString(memberRefreshRequest);

    mockMvc.perform(
            post("/members/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "MemberController/signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리플레쉬 토큰")
                ),
                responseFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("accessToken"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리플레쉬 토큰"),
                    fieldWithPath("isCertified").type(JsonFieldType.BOOLEAN).description("인증 여부")
                )
            )
        );
  }
}