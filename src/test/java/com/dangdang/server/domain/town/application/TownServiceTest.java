package com.dangdang.server.domain.town.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dangdang.server.domain.town.domain.AdjacentTownRepository;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.AdjacentTown;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.dto.AdjacentTownResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TownServiceTest {

  @Autowired
  private TownService townService;
  @Autowired
  private TownRepository townRepository;
  @Autowired
  private AdjacentTownRepository adjacentTownRepository;

  @Test
  @DisplayName("Town 테이블에서 거리 레벨에 따른 인접 동들을 mongoDB로 저장할 수 있다.")
  public void makeAdjacentTowns() throws Exception {
    //given
    townService.analyzeAdjacentTowns();
    List<Town> towns = townRepository.findAll();
    // when
    List<AdjacentTown> adjacentTowns = adjacentTownRepository.findAll();
    //then
    assertThat(adjacentTowns).hasSize(towns.size());
  }

  @ParameterizedTest
  @CsvSource(value = {"1:1", "2:2", "3:4", "4:6"}, delimiter = ':')
  @DisplayName("Town 이름과 Range level로 인접 지역 id 리스트를 조회할 수 있다.")
  public void findAdjacentTownWithRangeLevel(String level, int distance) throws Exception {
    //given
    String townName = "천호동";
    // when
    List<Long> ids = townService.findAdjacentTownWithRangeLevel(townName, level);
    Town town = townRepository.findByName(townName).orElseThrow();

    List<AdjacentTownResponse> adjacentTowns = townRepository.findAdjacentTownsByPoint(
        town.getLatitude(), town.getLongitude(), distance);

    //then
    List<Long> townIdsFromTable = adjacentTowns.stream().map(AdjacentTownResponse::getTownId)
        .toList();
    assertThat(ids).hasSize(adjacentTowns.size());
    assertThat(ids).usingRecursiveComparison().isEqualTo(townIdsFromTable);
  }
}
