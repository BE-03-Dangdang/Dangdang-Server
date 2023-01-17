package com.dangdang.server.domain.memberTown.application;

import com.dangdang.server.domain.memberTown.domain.entity.MemberTownRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberTownService {

  private final MemberTownRepository memberTownRepository;

  public MemberTownService(MemberTownRepository memberTownRepository) {
    this.memberTownRepository = memberTownRepository;
  }


}
