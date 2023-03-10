package com.dangdang.server.domain.memberTown.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dangdang.server.domain.common.StatusType;
import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.domain.entity.RangeType;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownCertifyRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownCertifyResponse;
import com.dangdang.server.domain.memberTown.exception.MemberTownNotFoundException;
import com.dangdang.server.domain.memberTown.exception.NotAppropriateCountException;
import com.dangdang.server.domain.memberTown.exception.NotAppropriateRangeException;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.domain.TownRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberTownServiceTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TownRepository townRepository;

  @Autowired
  MemberTownRepository memberTownRepository;

  @Autowired
  MemberTownService memberTownService;

  Member member;
  Long memberId;

  @BeforeEach
  void setup() {
    member = new Member("01012345678", null, "Albatross");
    Member savedMember = memberRepository.save(member);
    memberId = savedMember.getId();
  }

  @AfterEach
  void clear() {
    memberRepository.deleteById(member.getId());
  }

  @Test
  @DisplayName("?????? ????????? ?????? ??????")
  @Transactional
  void createMemberTown() {
    // given
    // member-town 1??? ???????????? ????????? (????????? ??? ?????? ?????? ??????)
    Town existingTown = townRepository.findByName("??????2???").get();
    MemberTown existingMemberTown = new MemberTown(member, existingTown);
    memberTownRepository.save(existingMemberTown);

    MemberTownRequest memberTownRequest = new MemberTownRequest("??????1???");

    // when
    memberTownService.createMemberTown(memberTownRequest, memberId);

    // then
    // memberTown list size ??? 2????????? ??????, ????????? ?????????????????? ??????
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(member.getId());
    assertThat(memberTownList.size()).isEqualTo(2);

    assertThat(memberTownList.get(0).getStatus()).isEqualTo(StatusType.INACTIVE);
    assertThat(memberTownList.get(1).getStatus()).isEqualTo(StatusType.ACTIVE);

  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????? - ?????? ?????? ????????? 1??? ?????? ??????")
  void createMemberTown_fail() {
    // given
    MemberTownRequest memberTownRequest = new MemberTownRequest("??????1???");

    // when, then
    assertThatThrownBy(() -> memberTownService.createMemberTown(memberTownRequest, memberId))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("?????? ?????? ?????? ??????- Inactive ????????? ??????")
  @Transactional
  void deleteMemberTown_withInactive() {
    // given
    // member-town 2??? ????????? ??? (existingMemberTown1??? Inactive ??? ??????)
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("??????2???").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    MemberTownRequest memberTownRequest = new MemberTownRequest("??????1???");

    // when
    memberTownService.deleteMemberTown(memberTownRequest, memberId);

    // then
    // size ??? 1?????? ?????? 2????????? Active ??????
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(memberId);
    assertThat(memberTownList.size()).isEqualTo(1);
    assertThat(memberTownList.get(0).getTown().getName()).isEqualTo("??????2???");
    assertThat(memberTownList.get(0).getStatus()).isEqualTo(StatusType.ACTIVE);
  }

  @Test
  @DisplayName("?????? ????????? ?????? ?????? - Active ????????? ?????? ")
  @Transactional
  void deleteMemberTown_withActive() {
    // given
    // member-town 2??? ????????? ??? (existingMemberTown1??? Inactive ??? ??????)
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("??????2???").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    MemberTownRequest memberTownRequest = new MemberTownRequest("??????2???");

    // when
    memberTownService.deleteMemberTown(memberTownRequest, memberId);

    // then
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(memberId);
    assertThat(memberTownList.size()).isEqualTo(1);
    assertThat(memberTownList.get(0).getTown().getName()).isEqualTo("??????1???");
    assertThat(memberTownList.get(0).getStatus()).isEqualTo(StatusType.ACTIVE);
  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????? - ?????? ?????? ????????? 2?????? ?????? ??????")
  void deleteMemberTown_withMemberTownNotSize2() {
    // when
    MemberTownRequest memberTownRequest = new MemberTownRequest("??????1???");

    // then, given
    assertThatThrownBy(() -> memberTownService.deleteMemberTown(memberTownRequest, memberId))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????? - ????????? ???????????? ????????? ????????? ??????")
  @Transactional
  void deleteMemberTown_withWrongMemberTownName() {
    // given
    // member-town 2??? ????????? ??? (existingMemberTown1??? Inactive ??? ??????)
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("??????2???").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    MemberTownRequest memberTownRequest = new MemberTownRequest("??????7???");

    // then, given
    assertThatThrownBy(() -> memberTownService.deleteMemberTown(memberTownRequest, memberId))
        .isInstanceOf(MemberTownNotFoundException.class);
  }

  @Test
  @DisplayName("?????? ?????? ????????? ?????? ??????")
  @Transactional
  void changeActiveMemberTown() {
    // given
    // member-town 2??? ????????? ??? (??????1?????? Inactive ??? ??????)
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    Town existingTown2 = townRepository.findByName("??????2???").get();
    MemberTown existingMemberTown2 = new MemberTown(member, existingTown2);
    memberTownRepository.save(existingMemberTown2);

    // ?????? 1????????? Active ?????? ??????
    MemberTownRequest memberTownRequest = new MemberTownRequest("??????1???");

    // when
    memberTownService.changeActiveMemberTown(memberTownRequest, memberId);

    // then
    // ?????? 1??? Active, ?????? 2??? Inactive
    List<MemberTown> memberTownList = memberTownRepository.findByMemberId(memberId);
    assertThat(memberTownList.get(0).getStatus()).isEqualTo(StatusType.ACTIVE);
    assertThat(memberTownList.get(1).getStatus()).isEqualTo(StatusType.INACTIVE);
  }

  @Test
  @DisplayName("?????? ?????? ????????? ?????? ?????? - ?????? ?????? ????????? 2?????? ?????? ??????")
  @Transactional
  void changeActiveMemberTown_withMemberTownNotSize2() {
    // given
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    MemberTownRequest memberTownRequest = new MemberTownRequest("??????2???");

    // when, then
    assertThatThrownBy(() -> memberTownService.changeActiveMemberTown(memberTownRequest, memberId))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("?????? ?????? range ?????? ??????")
  @Transactional
  void changeMemberTownRange() {
    // given
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("??????1???", 3);

    // when
    memberTownService.changeMemberTownRange(memberTownRangeRequest, memberId);

    // then
    // range ??? ?????? ???????????? ??????
    Town foundTown = townRepository.findByName("??????1???").get();
    MemberTown foundMemberTown = memberTownRepository.findByMemberIdAndTownId(
        member.getId(), foundTown.getId()).get();

    assertThat(foundMemberTown.getRangeType()).isEqualTo(RangeType.LEVEL3);
  }

  @Test
  @DisplayName("?????? ?????? range ?????? ?????? - ?????? ?????? ?????? ??????")
  @Transactional
  void changeMemberTownRange_withWrongMemberTown() {
    // given
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("??????2???", 3);

    // when, then
    assertThatThrownBy(
        () -> memberTownService.changeMemberTownRange(memberTownRangeRequest, memberId))
        .isInstanceOf(MemberTownNotFoundException.class);
  }

  // ?????? 2 - range ????????? ????????? ??????
  @Test
  @DisplayName("?????? ?????? range ?????? ?????? - range ????????? ????????? ??????")
  @Transactional
  void changeMemberTownRange_withWrongMemberTownRange() {
    // given
    Town existingTown1 = townRepository.findByName("??????1???").get();
    MemberTown existingMemberTown1 = new MemberTown(member, existingTown1);
    memberTownRepository.save(existingMemberTown1);

    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("??????1???", 7);

    // when, then
    assertThatThrownBy(
        () -> memberTownService.changeMemberTownRange(memberTownRangeRequest, memberId))
        .isInstanceOf(NotAppropriateRangeException.class);
  }

  @Test
  @DisplayName("??? ?????? ?????? ??????")
  @Transactional
  void certifyMemberTown_success() {
    // given
    Town existingTown = townRepository.findByName("??????1???").get();
    MemberTown memberTown = new MemberTown(member, existingTown);
    memberTownRepository.save(memberTown);

    MemberTownCertifyRequest memberTownCertifyRequest = new MemberTownCertifyRequest(
        BigDecimal.valueOf(127.0738380000), BigDecimal.valueOf(37.6248740000));

    // when
    MemberTownCertifyResponse memberTownCertifyResponse = memberTownService.certifyMemberTown(
        memberTownCertifyRequest, memberId);

    // then
    assertThat(memberTownCertifyResponse.isCertified()).isEqualTo(true);
  }

  @Test
  @DisplayName("??? ?????? ?????? ?????? - ?????? ??????????????? ????????? ??? ????????? ?????? ??????")
  @Transactional
  void certifyMemberTown_fail_byMemberTownNotInMyPositionRanges() {
    // given
    Town existingTown = townRepository.findByName("??????1???").get();
    MemberTown memberTown = new MemberTown(member, existingTown);
    memberTownRepository.save(memberTown);

    // ?????? ?????? ?????? 1?????? ??????
    MemberTownCertifyRequest memberTownCertifyRequest = new MemberTownCertifyRequest(
        BigDecimal.valueOf(127.0625320000), BigDecimal.valueOf(37.5144424000));

    // when
    MemberTownCertifyResponse memberTownCertifyResponse = memberTownService.certifyMemberTown(
        memberTownCertifyRequest, memberId);

    // then
    assertThat(memberTownCertifyResponse.isCertified()).isEqualTo(false);
  }

  @Test
  @DisplayName("??? ?????? ?????? ?????? - Active ????????? member town ??? ?????? ??????")
  @Transactional
  void certifyMemberTown_fail_byNotSettingActiveMemberTown() {
    // given
    Town existingTown = townRepository.findByName("??????1???").get();
    MemberTown memberTown = new MemberTown(member, existingTown);
    memberTown.updateMemberTownStatus(StatusType.INACTIVE);
    memberTownRepository.save(memberTown);

    // ?????? ?????? ?????? 1?????? ??????
    MemberTownCertifyRequest memberTownCertifyRequest = new MemberTownCertifyRequest(
        BigDecimal.valueOf(127.0738380000), BigDecimal.valueOf(37.6248740000));

    // when, then
    assertThatThrownBy(() -> memberTownService.certifyMemberTown(memberTownCertifyRequest, memberId))
        .isInstanceOf(MemberTownNotFoundException.class);
  }
}