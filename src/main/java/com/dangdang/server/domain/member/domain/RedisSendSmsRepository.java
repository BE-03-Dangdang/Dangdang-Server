package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.RedisSendSms;
import org.springframework.data.repository.CrudRepository;

public interface RedisSendSmsRepository extends CrudRepository<RedisSendSms, String> {

}
