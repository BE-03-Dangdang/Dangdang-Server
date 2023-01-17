package com.dangdang.server.domain.town.domain.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TownRepository extends JpaRepository<Town, Long> {

  Optional<Town> findByName(String townName);
}
