package com.dangdang.server.domain.member.application;

import static com.dangdang.server.domain.member.dto.request.SmsRequest.toRedisSms;

import com.dangdang.server.domain.member.domain.RedisSmsRepository;
import com.dangdang.server.domain.member.domain.entity.RedisSms;
import com.dangdang.server.domain.member.dto.request.SmsRequest;
import java.util.Random;
import net.nurigo.sdk.NurigoApp;
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
  public String sendMessage(SmsRequest smsRequest) {
    String authCode = generateAuthCode();

    RedisSms redisSms = toRedisSms(smsRequest, authCode);
    redisSmsRepository.save(redisSms);
/*

    String text =
        "[Web 발신]\n" + "[당근마켓] 인증번호 [" + authCode + "] *타인에게 절대 알리지 마세요. (계정 도용 위험)";

    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(smsRequest.getToPhoneNumber());
    message.setText(text);
    return defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
*/

    return authCode;
  }

  public String generateAuthCode() {
    random.setSeed(System.currentTimeMillis());
    StringBuilder sb = new StringBuilder();
    int sixth_digit = random.nextInt(10);
    int fifth_digit = random.nextInt(10);
    int fourth_digit = random.nextInt(10);
    int third_digit = random.nextInt(10);
    int second_digit = random.nextInt(10);
    int first_digit = random.nextInt(10);
    sb.append(sixth_digit).append(fifth_digit).append(fourth_digit).append(third_digit)
        .append(second_digit).append(first_digit);
    return sb.toString();
  }

}
