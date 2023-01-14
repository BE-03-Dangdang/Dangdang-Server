package com.dangdang.server.domain.member.controller;

import com.dangdang.server.domain.member.dto.MemberCertifiedRequestDto;
import com.dangdang.server.domain.member.service.SmsMessageService;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

  private final SmsMessageService smsMessageService;

  public MemberController(SmsMessageService smsMessageService) {
    this.smsMessageService = smsMessageService;
  }

  @PostMapping("/send")
  public ResponseEntity<Null> send(
      @RequestBody @Valid MemberCertifiedRequestDto memberCertifiedRequestDto) {
    smsMessageService.sendMessage(memberCertifiedRequestDto.getToNumber());
    return ResponseEntity.ok(null);
  }
}
