package com.dangdang.server.domain.pay.kftc.feignClient.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OpenBankingMemberRepository extends JpaRepository<OpenBankingMember, Long> {

  Optional<OpenBankingMember> findByState(String state);

  Optional<OpenBankingMember> findByPayMemberId(@Param(value = "payMemberId") Long payMemberId);
}
