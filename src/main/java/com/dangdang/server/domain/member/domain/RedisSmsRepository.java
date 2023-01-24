package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.RedisSms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisSmsRepository extends CrudRepository<RedisSms, String> {

}
