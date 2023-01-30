package com.dangdang.server.domain.member.domain;

import com.dangdang.server.domain.member.domain.entity.RedisSmsTen;
import org.springframework.data.repository.CrudRepository;

public interface RedisSmsTenRepository extends CrudRepository<RedisSmsTen, String> {

}
