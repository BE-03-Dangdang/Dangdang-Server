package com.dangdang.server.domain.town.domain;

import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.dto.AdjacentTownResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TownRepository extends JpaRepository<Town, Long> {
  Optional<Town> findByName(String townName);
  
  @Query(value =
      """
          SELECT t.town_id as townId, t.name, 
          (6371 * acos ( cos ( radians(:latitude) )  * cos( radians( latitude) ) 
          * cos( radians( longitude) - radians(:longitude) )  + sin ( radians(:latitude) ) 
          * sin(radians(latitude)))) as distance 
          FROM town t 
          HAVING distance <= :distanceLevel 
          ORDER BY name 
          """, nativeQuery = true)
  List<AdjacentTownResponse> findAdjacentTownsByPoint(@Param("latitude") BigDecimal latitude,
      @Param("longitude") BigDecimal longitude,
      @Param("distanceLevel") int distanceLevel);

  @Query(value =
      """
          SELECT t.town_id as townId, t.name, t.longitude, t.latitude, ST_Distance_Sphere(POINT(:longitude, :latitude), 
          POINT(t.longitude, t.latitude)) as distance from town t where ST_Distance_Sphere(POINT( 
          :longitude, :latitude), POINT(t.longitude, t.latitude)) <= 
          :distanceLevel ORDER BY t.name
          """, nativeQuery = true)
  List<AdjacentTownResponse> findTowns(@Param("longitude") BigDecimal longitude,
      @Param("latitude") BigDecimal latitude, @Param("distanceLevel") int distanceLevel);
}
