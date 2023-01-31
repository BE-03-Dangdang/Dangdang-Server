package com.dangdang.server.domain.memberTown.application;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.memberTown.domain.entity.TownAuthStatus;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownCertifyRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownCertifyResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownRangeResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownResponse;
import com.dangdang.server.domain.memberTown.exception.MemberTownNotFoundException;
import com.dangdang.server.domain.memberTown.exception.NotAppropriateCountException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.dto.AdjacentTownResponse;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberTownService {

  private final int MY_TOWN_CERTIFY_DISTANCE = 10;
  private final MemberTownRepository memberTownRepository;
  private final TownRepository townRepository;
  private final MemberRepository memberRepository;

  public MemberTownService(MemberTownRepository memberTownRepository,
      TownRepository townRepository, MemberRepository memberRepository) {
    this.memberTownRepository = memberTownRepository;
    this.townRepository = townRepository;
    this.memberRepository = memberRepository;
  }


  @Transactional
  public MemberTownResponse createMemberTown(MemberTownRequest memberTownRequest, Member member) {

    // 반드시 memberTown 개수가 1인 경우에만 추가 가능
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    if (memberTownList.size() != 1) {
      throw new NotAppropriateCountException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }

    Town foundTown = getTownByTownName(memberTownRequest.townName());
    Member foundMember = getMemberByMemberId(member.getId());

    MemberTown memberTown = new MemberTown(foundMember, foundTown);

    // 추가된 것으로 Active, 기존의 것은 Inactive
    MemberTown existingMemberTown = memberTownList.get(0);
    existingMemberTown.updateMemberTownStatus(StatusType.INACTIVE);
    memberTownRepository.save(memberTown);

    return new MemberTownResponse(memberTownRequest.townName());
  }

  @Transactional
  public void deleteMemberTown(MemberTownRequest memberTownRequest, Member member) {
    // MemberTown 이 2개 있어야만 삭제가 가능하다
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    if (memberTownList.size() != 2) {
      throw new NotAppropriateCountException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }

    MemberTown memberTown1 = memberTownList.get(0);
    MemberTown memberTown2 = memberTownList.get(1);

    // Inactive 지운 경우 -> Active 만 남게 된다 (삭제)
    // Active 지운 경우 -> Inactive 가 Active 로 변경됨  (삭제 + 변경)
    if (memberTown1.getMemberTownName().equals(memberTownRequest.townName())) {
      if (memberTown1.getStatus() == StatusType.ACTIVE) {
        memberTown2.updateMemberTownStatus(StatusType.ACTIVE);
      }
      memberTownRepository.delete(memberTown1);
    } else if (memberTown2.getMemberTownName().equals(memberTownRequest.townName())) {
      if (memberTown2.getStatus() == StatusType.ACTIVE) {
        memberTown1.updateMemberTownStatus(StatusType.ACTIVE);
      }
      memberTownRepository.delete(memberTown2);
    } else {
      throw new MemberTownNotFoundException(ExceptionCode.MEMBER_TOWN_NOT_FOUND);
    }
  }

  @Transactional
  public MemberTownResponse changeActiveMemberTown(MemberTownRequest memberTownRequest,
      Member member) {
    // 상대편은 Inactive, 입력 들어오면 Active
    // member 가 DB에 저장될 때 id를 반드시 가지고 있어야 한다
    // 이 메서드는 front 코드에서 모두 2개의 값이 있을 때 활성화 되야 함
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    if (memberTownList.size() != 2) {
      throw new NotAppropriateCountException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }
    for (MemberTown memberTown : memberTownList) {
      // 요구되는 이름인 경우 -> active
      if (memberTown.getMemberTownName().equals(memberTownRequest.townName())) {
        memberTown.updateMemberTownStatus(StatusType.ACTIVE);
      } else {
        memberTown.updateMemberTownStatus(StatusType.INACTIVE);
      }
    }
    return new MemberTownResponse(memberTownRequest.townName());
  }

  @Transactional
  public MemberTownRangeResponse changeMemberTownRange(
      MemberTownRangeRequest memberTownRangeRequest, Member member) {
    Member foundMember = getMemberByMemberId(member.getId());
    Town foundTown = getTownByTownName(memberTownRangeRequest.townName());

    MemberTown foundMemberTown = memberTownRepository
        .findByMemberIdAndTownId(foundMember.getId(), foundTown.getId())
        .orElseThrow(() -> new MemberTownNotFoundException(ExceptionCode.MEMBER_TOWN_NOT_FOUND));

    RangeType updatedRangeType = RangeType.getRangeType(memberTownRangeRequest.level());
    foundMemberTown.updateMemberTownRange(updatedRangeType);

    return new MemberTownRangeResponse(memberTownRangeRequest.townName(),
        memberTownRangeRequest.level());
  }

  @Transactional
  public MemberTownCertifyResponse certifyMemberTown(
      MemberTownCertifyRequest memberTownCertifyRequest, Member member) {
    boolean isCertified = false;
    // 1. 현재 위도, 경도를 기준으로 Town list 조회
    List<AdjacentTownResponse> towns = townRepository.findAdjacentTownsByPoint(
        memberTownCertifyRequest.longitude(),
        memberTownCertifyRequest.latitude(), MY_TOWN_CERTIFY_DISTANCE);
    // 2. Active 로 설정한 동네가 list 안에 있는지 확인
    MemberTown activeMemberTown = memberTownRepository.findByMemberId(member.getId())
        .stream()
        .filter(memberTown -> memberTown.getStatus() == StatusType.ACTIVE)
        .findFirst()
        .orElseThrow(() -> new MemberTownNotFoundException(ExceptionCode.MEMBER_TOWN_NOT_FOUND));

    for (AdjacentTownResponse adjacentTown : towns) {
      if (activeMemberTown.getMemberTownName().equals(adjacentTown.getName())) {
        isCertified = true;
        activeMemberTown.updateMemberTownAuthStatus(TownAuthStatus.TOWN_CERTIFIED);
        break;
      }
    }
    return new MemberTownCertifyResponse(isCertified);
  }

  private Member getMemberByMemberId(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(ExceptionCode.MEMBER_NOT_FOUND));
  }

  private Town getTownByTownName(String townName) {
    return townRepository.findByName(townName)
        .orElseThrow(() -> new TownNotFoundException(ExceptionCode.TOWN_NOT_FOUND));
  }


}
