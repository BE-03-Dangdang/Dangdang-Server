package com.dangdang.server.domain.chatroom.dto.response;

import java.time.LocalDateTime;

public record ChatRoomResponse(
    Long roomId,
    String profileImage,
    String nickName,
    String townName,
    String recentMessage,
    LocalDateTime recentMessageCreatedAt) {

}
