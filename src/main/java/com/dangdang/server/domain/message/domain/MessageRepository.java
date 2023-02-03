package com.dangdang.server.domain.message.domain;

import com.dangdang.server.domain.message.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
