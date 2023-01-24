package com.dangdang.server.domain.memberTown.domain;

import com.dangdang.server.domain.memberTown.domain.entity.MemberTown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTownRepository extends JpaRepository<MemberTown, Long> {

  List<MemberTown> findByMemberId(Long id);
}
