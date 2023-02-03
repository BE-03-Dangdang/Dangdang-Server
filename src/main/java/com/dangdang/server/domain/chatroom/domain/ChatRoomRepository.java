package com.dangdang.server.domain.chatroom.domain;

import com.dangdang.server.domain.chatroom.domain.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  @Query("select c from ChatRoom c where c.buyer.id = :memberId or c.seller.id = :memberId")
  List<ChatRoom> findByBuyerIdOrSellerId(Long memberId);
}
