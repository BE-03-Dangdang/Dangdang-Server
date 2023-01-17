package com.dangdang.server.domain.member.application;

import static com.dangdang.server.domain.member.dto.request.MemberSignUpRequest.toMember;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.response.MemberSignUpResponse;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.entity.TownRepository;
import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.utill.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final TownRepository townRepository;
  private final MemberTownRepository memberTownRepository;

  public MemberService(MemberRepository memberRepository, TownRepository townRepository,
      MemberTownRepository memberTownRepository) {
    this.memberRepository = memberRepository;
    this.townRepository = townRepository;
    this.memberTownRepository = memberTownRepository;
  }

  // orElseGet 메소드는 해당 값이 null 일때만 실행된다.
  @Transactional
  public MemberSignUpResponse signup(MemberSignUpRequest memberSignUpRequest) {
    // 이미 DB에 회원이 있는 경우
    if (memberRepository.existsByPhoneNumber(memberSignUpRequest.getPhoneNumber())) {
      return new MemberSignUpResponse(memberSignUpRequest.getPhoneNumber());
    }
    Member member = toMember(memberSignUpRequest);
    memberRepository.save(member);

    Town town = townRepository.findByName(memberSignUpRequest.getTownName())
        .orElseThrow(() -> new BusinessException(ExceptionCode.TOWN_NOT_FOUND));

    MemberTown memberTown = new MemberTown(member, town, RangeType.LEVEL1);
    memberTownRepository.save(memberTown);

    return new MemberSignUpResponse(member.getPhoneNumber());

  }

  public void certify(String toNumber, String randomNumber) {
    String code = RedisUtil.getData(toNumber);

    if (!code.equals(randomNumber)) {
      throw new BusinessException(ExceptionCode.CERTIFIED_FAIL);
    }

    RedisUtil.deleteData(toNumber);
    RedisUtil.setDataExpire(toNumber, "true", 3600L);
  }
}
