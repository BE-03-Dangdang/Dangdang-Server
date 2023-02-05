package com.dangdang.server.domain.message.dto;

public record ChatMessage(Long chatRoomId, String senderNickName, String message) {

}

