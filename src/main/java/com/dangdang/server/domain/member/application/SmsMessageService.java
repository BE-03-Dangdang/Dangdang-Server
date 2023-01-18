package com.dangdang.server.domain.member.application;

import static com.dangdang.server.domain.member.dto.request.SmsRequest.*;

import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import java.util.Random;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SmsMessageService {

  private final RedisSmsRepository redisSmsRepository;
  private String fromNumber;
  private final DefaultMessageService defaultMessageService;
  private Random random = new Random();

  public SmsMessageService(RedisSmsRepository redisSmsRepository, @Value("${secret.coolsms.apikey}") String apiKey,
      @Value("${secret.coolsms.apiSecret}") String apiSecret,
      @Value("${secret.coolsms.fromNumber}") String fromNumber) {
    this.redisSmsRepository = redisSmsRepository;
    this.fromNumber = fromNumber;
    this.defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret,
        "https://api.coolsms.co.kr");
  }

  @Transactional
  public SingleMessageSentResponse sendMessage(SmsRequest smsRequest) {
    String authCode = generateAuthCode();

    RedisSms redisSms = toRedisSms(smsRequest, authCode);
    redisSmsRepository.save(redisSms);

    String text =
        "[Web 발신]\n" + "[당근마켓] 인증번호 [" + authCode + "] *타인에게 절대 알리지 마세요. (계정 도용 위험)";

    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(smsRequest.getToPhoneNumber());
    message.setText(text);

    return defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
  }

  private String generateAuthCode() {
    random.setSeed(System.currentTimeMillis());
    return String.valueOf(random.nextInt(1000000) % 1000000);
  }
}
