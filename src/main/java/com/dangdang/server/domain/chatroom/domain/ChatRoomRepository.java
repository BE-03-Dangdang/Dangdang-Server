package com.dangdang.server.domain.chatroom.domain;

import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
