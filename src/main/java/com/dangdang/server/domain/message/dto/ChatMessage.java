package com.dangdang.server.domain.message.dto;

public record ChatMessage(Long chatRoomId, Long senderId, String message) {

}

