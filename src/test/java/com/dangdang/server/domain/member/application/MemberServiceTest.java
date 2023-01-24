package com.dangdang.server.domain.member.application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.entity.TownRepository;
import com.dangdang.server.global.security.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private TownRepository townRepository;
  @Mock
  private MemberTownRepository memberTownRepository;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private RedisSmsRepository redisSmsRepository;
  @Mock
  private RedisAuthCodeRepository redisAuthCodeRepository;
  @Mock
  private SmsMessageService smsMessageService;

  @Test
  @DisplayName("회원가입 하지 않은 유저가 phoneNumberCertifyRequest하면 memberCertifyResponse.getIsCertified()는 true다")
  void signupCertify() {
    //given
    String phoneNumber = "01012345678";
    String authCode = "123456";
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    when(redisSmsRepository.findById(any())).thenReturn(
        Optional.of(new RedisSms(phoneNumber, authCode)));
    when(memberRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

    //when
    MemberCertifyResponse memberCertifyResponse = memberService.signupCertify(
        phoneNumberCertifyRequest);

    //then
    assertThat(memberCertifyResponse.getIsCertified(), is(true));
  }

  @Test
  @DisplayName("회원가입을 위한 인증 코드 중 잘못된 코드를 입력하면 401 예외가 발생한다.")
  void signupCertify401() {
    //given
    String phoneNumber = "01012345678";
    String authCode = "123456";
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    when(redisSmsRepository.findById(any())).thenReturn(
        Optional.of(new RedisSms(phoneNumber, "000000")));

    //when

    //then
    assertThrows(RuntimeException.class, () -> {
      memberService.signupCertify(phoneNumberCertifyRequest);
    });
  }

  @Test
  @DisplayName("회원가입을 위한 인증 코드 중 이미 회원가입이 된 유저면 accessToken이 발급된다.")
  void signupCertify404() {
    //given
    String phoneNumber = "01012345678";
    String authCode = "123456";
    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    when(redisSmsRepository.findById(any())).thenReturn(
        Optional.of(new RedisSms(phoneNumber, authCode)));
    when(memberRepository.findByPhoneNumber(any())).thenReturn(
        Optional.of(new Member(1L, "cloudwi", "01012345678", null)));
    when(jwtTokenProvider.createAccessToken(1L)).thenReturn("123");
    //when
    MemberCertifyResponse memberCertifyResponse = memberService.signupCertify(
        phoneNumberCertifyRequest);

    //then
    assertThat(memberCertifyResponse.getAccessToken().isEmpty(), is(false));
  }

  @Test
  @DisplayName("회원가입을 한 사용자가 PhoneNumberCertifyRequest하면  memberCertifyResponse.getAccessToken()에 토큰이 들어있다.")
  void loginCertify() {
    //given
    String phoneNumber = "01012345678";
    String authCode = "123456";

    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    when(redisSmsRepository.findById(any())).thenReturn(
        Optional.of(new RedisSms(phoneNumber, authCode)));
    when(memberRepository.findByPhoneNumber(any())).thenReturn(
        Optional.of(new Member(1L, "cloudwi", "01054327510", null)));
    when(jwtTokenProvider.createAccessToken(1L)).thenReturn("aec");

    //when
    MemberCertifyResponse memberCertifyResponse = memberService.loginCertify(
        phoneNumberCertifyRequest);

    //then
    verify(redisSmsRepository).findById(any());
    verify(memberRepository).findByPhoneNumber(any());
    verify(jwtTokenProvider).createAccessToken(1L);

    assertThat(memberCertifyResponse.getAccessToken().isEmpty(), is(false));
  }

  @Test
  @DisplayName("회원가입 하지 않은 사용자가 loginCertify를 요청하면 404 예외가 발생한다. ")
  void loginCertify404() {
    //given
    String phoneNumber = "01012345678";
    String authCode = "123456";

    PhoneNumberCertifyRequest phoneNumberCertifyRequest = new PhoneNumberCertifyRequest(phoneNumber,
        authCode);

    when(redisSmsRepository.findById(any())).thenReturn(
        Optional.of(new RedisSms(phoneNumber, authCode)));
    when(memberRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

    //when

    //then
    assertThrows(RuntimeException.class,
        () -> memberService.loginCertify(phoneNumberCertifyRequest));
  }

  @Test
  @DisplayName("memberSignUpRequest하면 memberCertifyResponse 토큰을 응답한다.")
  void signup() {
    //given
    String townName = "삼성동";
    String nickname = "cloudwi";
    String phoneNumber = "01012345678";

    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(townName, nickname,
        phoneNumber, null);

    when(redisAuthCodeRepository.findById(any())).thenReturn(
        Optional.of(new RedisAuthCode(phoneNumber, true)));
    when(townRepository.findByName(any())).thenReturn(
        Optional.of(new Town("강남동", BigDecimal.valueOf(11L), BigDecimal.valueOf(11L))));
    when(memberRepository.save(any())).thenReturn(new Member(1L, "cloudwi", "01012345678", null));
    when(jwtTokenProvider.createAccessToken(1L)).thenReturn("aec");

    //when
    MemberCertifyResponse memberCertifyResponse = memberService.signup(memberSignUpRequest);

    //then
    assertThat(memberCertifyResponse.getAccessToken().isEmpty(), is(false));
  }

  @Test
  @DisplayName("인증코드를 잘못 입력하면 redisAuthCode가 조회 되지 않고 401 예외가 발생한다.")
  void signup401() {
    //given
    String townName = "삼성동";
    String nickname = "cloudwi";
    String phoneNumber = "01012345678";

    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(townName, nickname,
        phoneNumber, null);

    when(redisAuthCodeRepository.findById(any())).thenReturn(
        Optional.empty());
    //when

    //then
    assertThrows(RuntimeException.class, () -> memberService.signup(memberSignUpRequest));
  }

  @Test
  @DisplayName("회원가입 시 비정상적인 위치를 입력하면 404 예외가 발생한다.")
  void signup404() {
    //given
    String townName = "삼성동";
    String nickname = "cloudwi";
    String phoneNumber = "01012345678";

    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(townName, nickname,
        phoneNumber, null);

    when(redisAuthCodeRepository.findById(any())).thenReturn(
        Optional.of(new RedisAuthCode(phoneNumber, true)));
    when(townRepository.findByName(any())).thenReturn(
        Optional.empty());
    //when

    //then
    assertThrows(RuntimeException.class, () -> memberService.signup(memberSignUpRequest));
  }
}