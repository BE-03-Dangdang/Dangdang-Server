package com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain;

import com.dangdang.server.domain.pay.daangnpay.domain.payMember.domain.entity.PayMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PayMemberRepository extends JpaRepository<PayMember, Long> {

  Optional<PayMember> findByMemberId(@Param(value = "memberId") Long memberId);
}
