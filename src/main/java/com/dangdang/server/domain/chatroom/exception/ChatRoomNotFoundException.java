package com.dangdang.server.domain.chatroom.exception;

import com.dangdang.server.global.exception.BusinessException;
import com.dangdang.server.global.exception.ExceptionCode;

public class ChatRoomNotFoundException extends BusinessException {

  public ChatRoomNotFoundException(ExceptionCode exceptionCode) {
    super(exceptionCode);
  }
}
