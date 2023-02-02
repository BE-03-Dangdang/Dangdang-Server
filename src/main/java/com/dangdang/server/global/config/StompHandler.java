package com.dangdang.server.global.config;

import com.dangdang.server.domain.message.Exception.InvalidTokenException;
import com.dangdang.server.global.exception.ExceptionCode;
import com.dangdang.server.global.security.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  public StompHandler(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
    if (stompHeaderAccessor.getCommand() == StompCommand.CONNECT) {
      String accessToken = stompHeaderAccessor.getFirstNativeHeader("AccessToken");
      if (!jwtTokenProvider.validateAccessToken(accessToken)) {
        throw new InvalidTokenException(ExceptionCode.INVALID_TOKEN);
      }
    }
    return message;
  }
}
