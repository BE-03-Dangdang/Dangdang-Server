package com.dangdang.server.controller.smsMessage;

import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smsMessage")
public class SmsMessageController {

  private final SmsMessageService smsMessageService;

  public SmsMessageController(SmsMessageService smsMessageService) {
    this.smsMessageService = smsMessageService;
  }

  @PostMapping()
  public ResponseEntity<SingleMessageSentResponse> sendSms(@RequestBody SmsRequest smsRequest) {
    SingleMessageSentResponse singleMessageSentResponse = smsMessageService.sendMessage(smsRequest);
    return ResponseEntity.ok(singleMessageSentResponse);
  }
}
