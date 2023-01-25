package com.dangdang.server.controller;

import com.dangdang.server.domain.member.domain.entity.Member;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 외부 API 연동 제외
 */
@RestController
@RequestMapping("/connection-account")
public class ConnectionAccountDatabaseController {

  private final ConnectionAccountDatabaseService connectionAccountDataBaseService;

  public ConnectionAccountDatabaseController(
      ConnectionAccountDatabaseService connectionAccountDataBaseService) {
    this.connectionAccountDataBaseService = connectionAccountDataBaseService;
  }

  /**
   * 연결계좌 추가 API
   */
  @PostMapping("")
  public ResponseEntity<HttpStatus> addConnectionAccount(Authentication authentication,
      @RequestBody AddConnectionAccountRequest addConnectionAccountRequest) {
    Long memberId = ((Member) authentication.getPrincipal()).getId();

    connectionAccountDataBaseService.addConnectionAccount(memberId,
        addConnectionAccountRequest);

    return ResponseEntity.ok().build();
  }
}
