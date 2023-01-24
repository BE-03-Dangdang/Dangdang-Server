package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByPhoneNumber(String phoneNumber);

  boolean existsByPhoneNumber(String phoneNumber);
}
