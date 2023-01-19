package com.dangdang.server.domain.payMember.domain.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PayMemberRepository extends JpaRepository<PayMember, Long> {

  Optional<PayMember> findByMember_Id(@Param(value = "memberId") Long memberId);
}
