package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.RedisSms;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RedisSmsRepository extends CrudRepository<RedisSms, String> {

  Optional<RedisSms> findByPhoneNumber(String phoneNumber);
}
