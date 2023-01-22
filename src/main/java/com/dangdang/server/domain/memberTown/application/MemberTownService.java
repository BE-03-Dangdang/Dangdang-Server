package com.dangdang.server.domain.memberTown.application;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownRangeResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownResponse;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.entity.TownRepository;
import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberTownService {

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
      throw new BusinessException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }

    Town foundTown = getTownByTownName(memberTownRequest.townName());
    Member foundMember = getMemberByMemberId(member.getId());

    MemberTown memberTown = new MemberTown(foundMember, foundTown);

    // 추가된 것으로 Active, 기존의 것은 Inactive
    memberTownList.get(0).updateMemberTownStatus(StatusType.INACTIVE);
    memberTownRepository.save(memberTown);

    return new MemberTownResponse(memberTownRequest.townName());
  }

  @Transactional
  public MemberTownResponse deleteMemberTown(MemberTownRequest memberTownRequest, Member member) {


    // MemberTown 이 2개 있어야만 삭제가 가능하다
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    if (memberTownList.size() != 2) {
      throw new BusinessException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }

    MemberTown memberTown1 = memberTownList.get(0);
    MemberTown memberTown2 = memberTownList.get(1);

    // Inactive 지운 경우 -> Active 만 남게 된다 (삭제)
    // Active 지운 경우 -> Inactive 가 Active 로 변경됨  (삭제 + 변경)
    if (memberTown1.getTown().getName().equals(memberTownRequest.townName())) {
      if (memberTown1.getStatus() == StatusType.ACTIVE) {
        memberTown2.updateMemberTownStatus(StatusType.ACTIVE);
      }
      memberTownRepository.delete(memberTown1);
    } else {
      if (memberTown2.getStatus() == StatusType.ACTIVE) {
        memberTown2.updateMemberTownStatus(StatusType.ACTIVE);
      }
      memberTownRepository.delete(memberTown2);
    }

    return new MemberTownResponse(memberTownRequest.townName());
  }

  @Transactional
  public MemberTownResponse changeActiveMemberTown(MemberTownRequest memberTownRequest, Member member) {
    // 상대편은 Inactive, 입력 들어오면 Active
    // member 가 DB에 저장될 때 id를 반드시 가지고 있어야 한다
    // 이 메서드는 front 코드에서 모두 2개의 값이 있을 때 활성화 되야 함
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    if (memberTownList.size() != 2) {
      throw new BusinessException(ExceptionCode.NOT_APPROPRIATE_COUNT);
    }
    for (MemberTown memberTown : memberTownList) {
      // 요구되는 이름인 경우 -> active
      if (memberTown.getTown().getName().equals(memberTownRequest.townName())) {
        memberTown.updateMemberTownStatus(StatusType.ACTIVE);
      } else {
        memberTown.updateMemberTownStatus(StatusType.INACTIVE);
      }
    }
    return new MemberTownResponse(memberTownRequest.townName());
  }

  @Transactional
  public MemberTownRangeResponse changeMemberTownRange(MemberTownRangeRequest memberTownRangeRequest, Member member) {
    Member foundMember = getMemberByMemberId(member.getId());
    Town foundTown = getTownByTownName(memberTownRangeRequest.townName());

    MemberTown foundMemberTown = memberTownRepository
        .findByMemberIdAndTownId(foundMember.getId(), foundTown.getId())
        .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_TOWN_NOT_FOUND));

    RangeType updatedRangeType = RangeType.getRangeType(memberTownRangeRequest.level());
    foundMemberTown.updateMemberTownRange(updatedRangeType);

    return new MemberTownRangeResponse(memberTownRangeRequest.townName(),
        memberTownRangeRequest.level());
  }


  private Member getMemberByMemberId(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
  }

  private Town getTownByTownName(String townName) {
    return townRepository.findByName(townName)
        .orElseThrow(() -> new BusinessException(ExceptionCode.TOWN_NOT_FOUND));
  }
}
