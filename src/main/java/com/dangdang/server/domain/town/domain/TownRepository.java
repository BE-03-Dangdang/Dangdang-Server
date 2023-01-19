package com.dangdang.server.domain.town.domain;

import com.dangdang.server.domain.town.domain.entity.Town;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TownRepository extends JpaRepository<Town, Long> {

  /*
    @author : 김영빈, 김기웅
    @description : 게시글 전체 조회 로직에서 조회 대상이 될 동 id를 가져오기 위해 임시로 작성한 메서드입니다.
    실제 동작하지 않으므로 편의에 맞게 수정해주세요. 인터페이스만 만들어둔 것입니다.
   */
  @Query("select t from Town t")
  List<Long> findAdjacencyTownId(@Param("townId") long townId, @Param(("range")) int range);

  Optional<Town> findTownByName(@Param("name") String name);
}
