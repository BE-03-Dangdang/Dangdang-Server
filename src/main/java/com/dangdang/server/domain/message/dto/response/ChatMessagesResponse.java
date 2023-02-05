package com.dangdang.server.domain.message.dto.response;

import com.dangdang.server.domain.message.dto.ChatMessage;
import java.util.List;

public record ChatMessagesResponse(List<ChatMessage> chatMessages) {

}
