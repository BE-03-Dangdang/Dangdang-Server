package com.dangdang.server.domain.memberTown.dto.response;

import lombok.Getter;

@Getter
public class MemberTownSaveResponse {

  private Long memberTownId;
  private String townName;

  public MemberTownSaveResponse(Long memberTownId, String townName) {
    this.memberTownId = memberTownId;
    this.townName = townName;
  }
}
