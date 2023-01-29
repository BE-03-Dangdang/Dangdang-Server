package com.dangdang.server.domain.town.domain;

import com.dangdang.server.domain.town.domain.entity.AdjacentTown;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdjacentTownRepository extends MongoRepository<AdjacentTown, String> {

}
