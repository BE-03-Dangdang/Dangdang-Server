package com.dangdang.server.controller.smsMessage;

import com.dangdang.server.domain.member.application.SmsMessageService;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import com.dangdang.server.domain.member.dto.response.TestSendMessageResponse;
import com.dangdang.server.domain.member.exception.SmsRequestException;
import com.dangdang.server.global.exception.ExceptionCode;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
  public ResponseEntity<TestSendMessageResponse> sendMessage(
      @RequestBody @Valid SmsRequest smsRequest, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new SmsRequestException(ExceptionCode.BAD_REQUEST);
    }

    String authCode = smsMessageService.sendMessage(smsRequest);
    TestSendMessageResponse testSendMessageResponse = new TestSendMessageResponse(authCode);
    return ResponseEntity.ok(testSendMessageResponse);
  }
}
