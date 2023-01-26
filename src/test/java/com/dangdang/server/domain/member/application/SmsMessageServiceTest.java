package com.dangdang.server.domain.member.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmsMessageServiceTest {

  @Autowired
  SmsMessageService smsMessageService;

  @Test
  void generateAuthCode() {
    for (int i = 0; i < 10; i++) {
      System.out.println(smsMessageService.generateAuthCode());
    }
  }

}