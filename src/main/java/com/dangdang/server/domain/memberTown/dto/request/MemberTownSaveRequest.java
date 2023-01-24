package com.dangdang.server.domain.memberTown.dto.request;

import lombok.Getter;

@Getter
public class MemberTownSaveRequest {

  private String townName;

  private MemberTownSaveRequest() {
  }

  public MemberTownSaveRequest(String townName) {
    this.townName = townName;
  }

}
