package com.dangdang.server.domain.memberTown.domain;

import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {

  List<MemberTown> findByMemberId(Long id);

  Optional<MemberTown> findByMemberIdAndTownId(Long memberId, Long townId);

}
