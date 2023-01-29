package com.dangdang.server.domain.town.domain;

import com.dangdang.server.domain.town.domain.entity.Range;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RangeRepository extends MongoRepository<Range, String> {

  @Query("{'$and':[ {'name' : ?0}, {'level': ?1} ] }")
  Optional<Range> findAdjacentTownIds(String townName, String level);
}
