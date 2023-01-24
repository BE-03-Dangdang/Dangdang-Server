package com.dangdang.server.domain.memberTown.application;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownSaveRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownSaveResponse;
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
  public MemberTownSaveResponse save(MemberTownSaveRequest memberTownSaveRequest, Member principal) {
    Member member = getMember(principal);

    if (memberTownRepository.findByMemberId(member.getId()).size() > 2) {
      throw new BusinessException(ExceptionCode.OVER_COUNT);
    }

    Town town = getTown(memberTownSaveRequest);

    MemberTown memberTown = new MemberTown(member, town);

    memberTownRepository.save(memberTown);

    return new MemberTownSaveResponse(memberTown.getId(), town.getName());
  }

  @Transactional
  public void delete(Long memberTownId, Member principal) {
    Member member = getMember(principal);

    if (memberTownRepository.findByMemberId(member.getId()).size() <= 1) {
      throw new BusinessException(ExceptionCode.OVER_COUNT);
    }

    MemberTown memberTown = getMemberTown(memberTownId);
    memberTown.isOwner(member.getId());

    memberTownRepository.deleteById(memberTownId);

    MemberTown updateMemberTown = new MemberTown(StatusType.INACTIVE);
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    memberTownList.get(0).update(updateMemberTown);
  }

  private MemberTown getMemberTown(Long memberTownId) {
    return memberTownRepository.findById(memberTownId)
        .orElseThrow(() -> new BusinessException(ExceptionCode.TOWN_NOT_FOUND));
  }

  private Member getMember(Member principal) {
    return memberRepository.findById(principal.getId())
        .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
  }

  private Town getTown(MemberTownSaveRequest memberTownSaveRequest) {
    return townRepository.findByName(memberTownSaveRequest.getTownName())
        .orElseThrow(() -> new BusinessException(ExceptionCode.TOWN_NOT_FOUND));
  }
}
