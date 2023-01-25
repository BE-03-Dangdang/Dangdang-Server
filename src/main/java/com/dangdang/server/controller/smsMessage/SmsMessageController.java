package com.dangdang.server.controller.smsMessage;

import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import com.dangdang.server.domain.member.dto.response.TestSendMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms-message")
public class SmsMessageController {

  private final SmsMessageService smsMessageService;

  public SmsMessageController(SmsMessageService smsMessageService) {
    this.smsMessageService = smsMessageService;
  }

  @PostMapping
  public ResponseEntity<TestSendMessageResponse> sendMessage(@RequestBody SmsRequest smsRequest) {
    String authCode = smsMessageService.sendMessage(smsRequest);
    TestSendMessageResponse testSendMessageResponse = new TestSendMessageResponse(authCode);
    return ResponseEntity.ok(testSendMessageResponse);
  }
}
