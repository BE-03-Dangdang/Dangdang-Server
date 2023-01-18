package com.dangdang.server.domain.member.application;

import static com.dangdang.server.domain.member.dto.request.MemberSignUpRequest.*;
import static com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest.*;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.request.PhoneNumberCertifyRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.entity.TownRepository;
import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.security.JwtTokenProvider;
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
    if (memberRepository.findByPhoneNumber(phoneNumberCertifyRequest.getPhoneNumber())
        .isPresent()) {
      Member member = memberRepository.findByPhoneNumber(
              phoneNumberCertifyRequest.getPhoneNumber())
          .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
      return getMemberCertifyResponse(member.getId());
    }

    RedisAuthCode redisAuthCode = toRedisAuthCode(
        phoneNumberCertifyRequest);
    redisAuthCodeRepository.save(redisAuthCode);

    return new MemberCertifyResponse(null,true);
  }

  @Transactional
  public MemberCertifyResponse loginCertify(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    phoneNumberCertify(phoneNumberCertifyRequest);

    Member member = memberRepository.findByPhoneNumber(phoneNumberCertifyRequest.getPhoneNumber())
        .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

    return getMemberCertifyResponse(member.getId());
  }

  @Transactional
  public MemberCertifyResponse signup(MemberSignUpRequest memberSignupRequest) {
    RedisAuthCode redisAuthCode = redisAuthCodeRepository.findById(
            memberSignupRequest.getPhoneNumber())
        .orElseThrow(() -> new BusinessException(ExceptionCode.CERTIFIED_FAIL));

    redisAuthCodeRepository.deleteById(redisAuthCode.getId());

    Member member = toMember(memberSignupRequest);
    member = memberRepository.save(member);

    Town town = townRepository.findByName(memberSignupRequest.getTownName())
        .orElseThrow(() -> new BusinessException(ExceptionCode.TOWN_NOT_FOUND));

    MemberTown memberTown = new MemberTown(member, town, RangeType.LEVEL1);
    memberTownRepository.save(memberTown);

    return getMemberCertifyResponse(member.getId());
  }

  private void phoneNumberCertify(PhoneNumberCertifyRequest phoneNumberCertifyRequest) {
    RedisSms redisSms = redisSmsRepository.findById(phoneNumberCertifyRequest.getPhoneNumber())
        .orElseThrow(() ->
            new BusinessException(ExceptionCode.CERTIFIED_FAIL)
        );

    String authCode = redisSms.getAuthCode();

    if (!authCode.equals(phoneNumberCertifyRequest.getAuthCode())) {
      throw new BusinessException(ExceptionCode.CERTIFIED_FAIL);
    }

    redisSmsRepository.deleteById(phoneNumberCertifyRequest.getPhoneNumber());
  }

  private MemberCertifyResponse getMemberCertifyResponse(Long memberId) {
    String accessToken = jwtTokenProvider.createAccessToken(memberId);
    return new MemberCertifyResponse(accessToken, true);
  }
}