package com.dangdang.server.domain.memberTown.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dangdang.server.domain.member.domain.MemberRepository;
import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.exception.MemberNotFoundException;
import com.dangdang.server.domain.memberTown.domain.MemberTownRepository;
import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRangeRequest;
import com.dangdang.server.domain.memberTown.dto.request.MemberTownRequest;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownRangeResponse;
import com.dangdang.server.domain.memberTown.dto.response.MemberTownResponse;
import com.dangdang.server.domain.memberTown.exception.MemberTownNotFoundException;
import com.dangdang.server.domain.memberTown.exception.NotAppropriateCountException;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberTownServiceUnitTest {

  @InjectMocks
  MemberTownService memberTownService;

  @Mock
  MemberRepository memberRepository;

  @Mock
  TownRepository townRepository;

  @Mock
  MemberTownRepository memberTownRepository;

  @Test
  @DisplayName("멤버 타운 생성 성공")
  void createMemberTown_success() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉2동");

    Optional<Town> optionalTown = Optional.of(
        new Town("공릉2동", BigDecimal.valueOf(12.2), BigDecimal.valueOf(13.2)));
    Optional<Member> optionalMember = Optional.of(member);

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(List.of(new MemberTown(member, existingTown)));
    when(townRepository.findByName(any())).thenReturn(optionalTown);
    when(memberRepository.findById(any())).thenReturn(optionalMember);

    // when
    MemberTownResponse memberTownResponse = memberTownService.createMemberTown(memberTownRequest,
        1L);

    // then
    assertThat(memberTownResponse.townName()).isEqualTo("공릉2동");
    // 실제 메서드가 수행되었는지 여부를 확인
    verify(memberTownRepository).findByMemberId(any());
    verify(townRepository).findByName(memberTownRequest.townName());
    verify(memberRepository).findById(member.getId());
    verify(memberTownRepository).save(any());
  }

  @Test
  @DisplayName("멤버 타운 생성 실패 - 멤버 타운 개수가 1이 아닌 경우")
  void createMemberTown_fail_byMemberTownSizeNot1() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉2동");
    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(Collections.emptyList());

    // when, then
    assertThatThrownBy(() -> memberTownService.createMemberTown(memberTownRequest, 1L))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("멤버 타운 생성 실패 - 타운을 찾지 못한 경우")
  void createMemberTown_fail_byNotFoundTown() {
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉7동");
    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(List.of(new MemberTown(member, existingTown)));
    when(townRepository.findByName(any())).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> memberTownService.createMemberTown(memberTownRequest, 1L))
        .isInstanceOf(TownNotFoundException.class);
  }

  @Test
  @DisplayName("멤버 타운 생성 실패 - 멤버를 찾지 못한 경우")
  void createMemberTown_fail_byNotFoundMember() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉2동");
    Optional<Town> optionalTown = Optional.of(
        new Town("공릉2동", BigDecimal.valueOf(12.2), BigDecimal.valueOf(13.2)));

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(List.of(new MemberTown(member, existingTown)));
    when(townRepository.findByName(any())).thenReturn(optionalTown);
    when(memberRepository.findById(any())).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> memberTownService.createMemberTown(memberTownRequest, 1L))
        .isInstanceOf(MemberNotFoundException.class);
  }

  @Test
  @DisplayName("멤버 타운 삭제 성공 - Inactive 삭제한 경우")
  void deleteMemberTown_success() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown1 = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    Town existingTown2 = new Town("공릉2동", BigDecimal.valueOf(14.1), BigDecimal.valueOf(10.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉2동");

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(
            List.of(new MemberTown(member, existingTown1), new MemberTown(member, existingTown2)));

    // when
    memberTownService.deleteMemberTown(memberTownRequest, 1L);

    // then
    verify(memberTownRepository).delete(any());
  }

  @Test
  @DisplayName("멤버 타운 삭제 실패 - 멤버 타운 사이즈가 2가 아닌 경우")
  void deleteMemberTown_fail_byMemberTownSizeNot2() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown1 = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉 1동");

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(List.of(new MemberTown(member, existingTown1)));

    // when, then
    assertThatThrownBy(() -> memberTownService.deleteMemberTown(memberTownRequest, 1L))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("멤버 타운 삭제 실패 - 잘못된 동네의 삭제 요청의 경우")
  void deleteMemberTown_fail_byWrongMemberTownName() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown1 = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    Town existingTown2 = new Town("공릉2동", BigDecimal.valueOf(14.1), BigDecimal.valueOf(10.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉7동");

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(
            List.of(new MemberTown(member, existingTown1), new MemberTown(member, existingTown2)));

    // when, then
    assertThatThrownBy(() -> memberTownService.deleteMemberTown(memberTownRequest, 1L))
        .isInstanceOf(MemberTownNotFoundException.class);
  }

  @Test
  @DisplayName("멤버 타운 활성화 변경 성공")
  void changeActiveMemberTown_success() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown1 = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    Town existingTown2 = new Town("공릉2동", BigDecimal.valueOf(14.1), BigDecimal.valueOf(10.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉1동");

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(
            List.of(new MemberTown(member, existingTown1), new MemberTown(member, existingTown2)));

    // when
    MemberTownResponse memberTownResponse = memberTownService
        .changeActiveMemberTown(memberTownRequest, 1L);

    // then
    assertThat(memberTownResponse.townName()).isEqualTo("공릉1동");
  }

  @Test
  @DisplayName("멤버 타운 활성화 변경 실패 - 멤버 타운 사이즈가 2가 아닌 경우")
  void changeActiveMemberTown_fail_byMemberTownSizeNot2() {
    // given
    Member member = new Member(1L, "01012345678", "Albatross");
    Town existingTown1 = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRequest memberTownRequest = new MemberTownRequest("공릉1동");

    when(memberTownRepository.findByMemberId(any()))
        .thenReturn(List.of(new MemberTown(member, existingTown1)));

    // when, then
    assertThatThrownBy(() -> memberTownService.changeActiveMemberTown(memberTownRequest, 1L))
        .isInstanceOf(NotAppropriateCountException.class);
  }

  @Test
  @DisplayName("멤버 타운 range 변경 성공")
  void changeMemberTownRange_success() {
    // given
    Member existingMember = new Member(1L, "01012345678", "Albatross");
    Town existingTown = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("공릉1동", 1);

    when(memberRepository.findById(any())).thenReturn(Optional.of(existingMember));
    when(townRepository.findByName(any())).thenReturn(Optional.of(existingTown));
    when(memberTownRepository.findByMemberIdAndTownId(any(), any()))
        .thenReturn(Optional.of(new MemberTown(existingMember, existingTown)));

    // when
    MemberTownRangeResponse memberTownRangeResponse = memberTownService.changeMemberTownRange(
        memberTownRangeRequest, 1L);

    // then
    assertThat(memberTownRangeResponse).usingRecursiveComparison()
        .isEqualTo(memberTownRangeRequest);
    verify(memberRepository).findById(any());
    verify(townRepository).findByName(any());
    verify(memberTownRepository).findByMemberIdAndTownId(any(), any());
  }

  @Test
  @DisplayName("멤버 타운 range 변경 실패 - 멤버 타운 찾을 수 없는 경우")
  void changeMemberTownRange_fail_byNotFoundMemberTown() {
    // given
    Member existingMember = new Member(1L, "01012345678", "Albatross");
    Town existingTown = new Town("공릉1동", BigDecimal.valueOf(11.1), BigDecimal.valueOf(12.1));
    MemberTownRangeRequest memberTownRangeRequest = new MemberTownRangeRequest("공릉1동", 1);

    when(memberRepository.findById(any())).thenReturn(Optional.of(existingMember));
    when(townRepository.findByName(any())).thenReturn(Optional.of(existingTown));
    when(memberTownRepository.findByMemberIdAndTownId(any(), any())).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(
        () -> memberTownService.changeMemberTownRange(memberTownRangeRequest, 1L))
        .isInstanceOf(MemberTownNotFoundException.class);
  }
}
