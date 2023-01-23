package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthCodeRepository extends CrudRepository<RedisAuthCode, String> {

}