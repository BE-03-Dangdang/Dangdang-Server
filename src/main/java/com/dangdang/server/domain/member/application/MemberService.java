package com.dangdang.server.domain.member.application;

import static com.dangdang.server.domain.member.dto.request.MemberSignUpRequest.toMember;
import static com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest.toRedisAuthCode;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.dto.request.MemberRefreshRequest;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import com.dangdang.server.domain.member.exception.MemberCertifiedFailException;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.member.exception.TownNotFoundException;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.security.JwtTokenProvider;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final TownRepository townRepository;
  private final MemberTownRepository memberTownRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisSmsRepository redisSmsRepository;
  private final RedisAuthCodeRepository redisAuthCodeRepository;

  public MemberService(MemberRepository memberRepository, TownRepository townRepository,
      MemberTownRepository memberTownRepository, JwtTokenProvider jwtTokenProvider,
      RedisSmsRepository redisSmsRepository, RedisAuthCodeRepository redisAuthCodeRepository) {
    this.memberRepository = memberRepository;
    this.townRepository = townRepository;
    this.memberTownRepository = memberTownRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.redisSmsRepository = redisSmsRepository;
    this.redisAuthCodeRepository = redisAuthCodeRepository;
  }

  @Transactional
  public MemberCertifyResponse signupCertify(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    phoneNumberCertify(phoneNumberCertifyRequest);

    // 시작하기 -> User DB에 있는 경우 -> token 발급
    Optional<Member> member = memberRepository.findByPhoneNumber(
        phoneNumberCertifyRequest.phoneNumber());

    if (member.isPresent()) {
      return getMemberCertifyResponse(member.get());
    }

    RedisAuthCode redisAuthCode = toRedisAuthCode(phoneNumberCertifyRequest);
    redisAuthCodeRepository.save(redisAuthCode);

    return MemberCertifyResponse.from(null, null, true);
  }

  @Transactional
  public MemberCertifyResponse loginCertify(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    phoneNumberCertify(phoneNumberCertifyRequest);

    Member member = memberRepository.findByPhoneNumber(phoneNumberCertifyRequest.phoneNumber())
        .orElseThrow(() -> new MemberNotFoundException(ExceptionCode.MEMBER_NOT_FOUND));

    return getMemberCertifyResponse(member);
  }

  @Transactional
  public MemberCertifyResponse signup(MemberSignUpRequest memberSignupRequest) {
    RedisAuthCode redisAuthCode = redisAuthCodeRepository.findById(
            memberSignupRequest.getPhoneNumber())
        .orElseThrow(() -> new MemberCertifiedFailException(ExceptionCode.CERTIFIED_FAIL));

    redisAuthCodeRepository.deleteById(redisAuthCode.getId());

    Member member = toMember(memberSignupRequest);
    member = memberRepository.save(member);

    Town town = townRepository.findByName(memberSignupRequest.getTownName())
        .orElseThrow(() -> new TownNotFoundException(ExceptionCode.TOWN_NOT_FOUND));

    MemberTown memberTown = new MemberTown(member, town);
    memberTownRepository.save(memberTown);

    return getMemberCertifyResponse(member);
  }

  private void phoneNumberCertify(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    RedisSms redisSms = redisSmsRepository.findById(phoneNumberCertifyRequest.phoneNumber())
        .orElseThrow(() ->
            new MemberCertifiedFailException(ExceptionCode.CERTIFIED_FAIL)
        );

    redisSms.isAuthCode(phoneNumberCertifyRequest.authCode());

    redisSmsRepository.deleteById(phoneNumberCertifyRequest.phoneNumber());
  }

  @Transactional
  public MemberCertifyResponse getMemberCertifyResponse(Member member) {
    String accessToken = jwtTokenProvider.createAccessToken(member.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

    member.setRefreshToken(refreshToken);

    return MemberCertifyResponse.from(accessToken, refreshToken, true);
  }

  @Transactional
  public MemberCertifyResponse refresh(MemberRefreshRequest memberRefreshRequest) {
    String refreshToken = memberRefreshRequest.refreshToken();

    if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
      throw new MemberCertifiedFailException(ExceptionCode.CERTIFIED_FAIL);
    }

    Member principal = ((Member) jwtTokenProvider.getRefreshTokenAuthentication(refreshToken)
        .getPrincipal());

    Member member = memberRepository.findById(principal.getId())
        .orElseThrow(() -> new MemberNotFoundException(ExceptionCode.MEMBER_NOT_FOUND));

    if (!member.getRefreshToken().equals(refreshToken)) {
      throw new MemberCertifiedFailException(ExceptionCode.CERTIFIED_FAIL);
    }

    return getMemberCertifyResponse(principal);
  }
}