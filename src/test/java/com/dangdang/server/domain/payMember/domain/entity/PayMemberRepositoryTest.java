package com.dangdang.server.domain.payMember.domain.entity;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.member.domain.entity.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PayMemberRepositoryTest {

  @Autowired
  PayMemberRepository payMemberRepository;
  @Autowired
  MemberRepository memberRepository;

  @Test
  @DisplayName("memberId FK값을 활용해서 PayMember 엔티티를 가져올 수 있다.")
  void findByMember_Id() {
    Member member = new Member("예지 테스트 유저", "01012341234");
    memberRepository.save(member);

    String password = "password123";
    PayMember payMember = new PayMember(password, member);
    payMemberRepository.save(payMember);

    PayMember findPayMember = payMemberRepository.findByMember_Id(member.getId()).get();

    Assertions.assertThat(findPayMember.getMember().getId()).isEqualTo(member.getId());
  }
}