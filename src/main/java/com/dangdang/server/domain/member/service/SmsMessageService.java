package com.dangdang.server.domain.member.service;

import java.util.Random;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsMessageService {

  private String fromNumber;

  private final DefaultMessageService defaultMessageService;

  public SmsMessageService(@Value("${secret.coolsms.apikey}") String apiKey,
      @Value("${secret.coolsms.apiSecret}") String apiSecret,
      @Value("${secret.coolsms.fromNumber}") String fromNumber) {
    this.fromNumber = fromNumber;
    this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret,
        "https://api.coolsms.co.kr");
  }

  public SingleMessageSentResponse sendMessage(String toNumber) {
    //redis에 넣어야함
    int code = generateAuthNo2();

    String text =
        "[Web 발신]\n" + "[당근마켓] 인증번호 [" + code + "] *타인에게 절대 알리지 마세요. (계정 도용 위험)";

    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(toNumber);
    message.setText(text);

    SingleMessageSentResponse singleMessageSentResponse =
        defaultMessageService.sendOne(new SingleMessageSendingRequest(message));

    return singleMessageSentResponse;
  }

  public int generateAuthNo2() {
    Random generator = new Random();
    generator.setSeed(System.currentTimeMillis());
    return generator.nextInt(1000000) % 1000000;
  }
}
