package com.dangdang.server.domain.memberTown.domain;

import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {

  List<MemberTown> findByMemberId(Long id);

  Optional<MemberTown> findByMemberIdAndTownId(Long memberId, Long townId);

  @Query(value = "select mt from MemberTown mt join fetch mt.member join fetch mt.town where mt.member.id = :memberId and mt.status = 'ACTIVE'")
  Optional<MemberTown> findActiveMemberTownByMember(@Param("memberId") Long memberId);

}
