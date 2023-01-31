package com.dangdang.server.controller.pay;

import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AllConnectionAccount;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetAllConnectionAccountResponse;
import com.dangdang.server.global.aop.CurrentUserId;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 외부 API 연동 제외
 */
@RestController
@RequestMapping("/connection-accounts")
public class ConnectionAccountDatabaseController {

  private final ConnectionAccountDatabaseService connectionAccountDataBaseService;

  public ConnectionAccountDatabaseController(
      ConnectionAccountDatabaseService connectionAccountDataBaseService) {
    this.connectionAccountDataBaseService = connectionAccountDataBaseService;
  }

  /**
   * 연결계좌 추가 API
   */
  @CurrentUserId
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("")
  public void addConnectionAccount(Long memberId,
      @RequestBody @Valid AddConnectionAccountRequest addConnectionAccountRequest) {
    connectionAccountDataBaseService.addConnectionAccount(memberId,
        addConnectionAccountRequest);
  }

  /**
   * 내 연결 계좌 리스트 제공 API
   */
  @CurrentUserId
  @GetMapping("")
  public GetAllConnectionAccountResponse getAllConnectionAccountResponse(Long memberId) {
    List<AllConnectionAccount> allConnectionAccount = connectionAccountDataBaseService.getAllConnectionAccount(
        memberId);
    return GetAllConnectionAccountResponse.from(allConnectionAccount);
  }
}
