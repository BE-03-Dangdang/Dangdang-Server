package com.dangdang.server.domain.town.application;

import static com.dangdang.server.global.exception.ExceptionCode.TOWN_NOT_FOUND;

import com.dangdang.server.domain.town.domain.AdjacentTownRepository;
import com.dangdang.server.domain.town.domain.RangeRepository;
import com.dangdang.server.domain.town.domain.TownRepository;
import com.dangdang.server.domain.town.domain.entity.AdjacentTown;
import com.dangdang.server.domain.town.domain.entity.Range;
import com.dangdang.server.domain.town.domain.entity.Town;
import com.dangdang.server.domain.town.dto.AdjacentTownResponse;
import com.dangdang.server.domain.town.dto.request.AdjacentTownRequest;
import com.dangdang.server.domain.town.exception.TownNotFoundException;
import com.dangdang.server.global.exception.ExceptionCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TownService {

  private static final Map<String, Integer> rangeLevels = Map.of("1", 1, "2", 2, "3", 4, "4", 6);

  private final TownRepository townRepository;
  private final AdjacentTownRepository adjacentTownRepository;
  private final RangeRepository rangeRepository;

  public TownService(TownRepository townRepository, AdjacentTownRepository adjacentTownRepository,
      RangeRepository rangeRepository) {
    this.townRepository = townRepository;
    this.adjacentTownRepository = adjacentTownRepository;
    this.rangeRepository = rangeRepository;
  }

  public void analyzeAdjacentTowns() {
    // 모든 town Id에 대해서 인접 document 생성
    List<Town> towns = townRepository.findAll();
    towns.forEach(town -> {
      Range[] ranges = new Range[4];
      for (String currLevel : rangeLevels.keySet()) {
        int distance = rangeLevels.get(currLevel);
        List<AdjacentTownResponse> adjacentTownResponses = townRepository.findAdjacentTownsByPoint(
            town.getLongitude(), town.getLatitude(), distance);

        List<Long> ids = adjacentTownResponses.stream().map(AdjacentTownResponse::getTownId)
            .toList();
        List<String> names = adjacentTownResponses.stream().map(AdjacentTownResponse::getName)
            .toList();
        Range save = rangeRepository.save(new Range(town.getName(), currLevel, ids, names));
        int rangeIndex = Integer.parseInt(currLevel) - 1;
        ranges[rangeIndex] = save;
      }
      AdjacentTown adjacentTown = new AdjacentTown(town.getName(), ranges);
      adjacentTownRepository.save(adjacentTown);
    });
  }

  public List<Long> findAdjacentTownIds(AdjacentTownRequest adjacentTownRequest) {
    Town town = townRepository.findById(adjacentTownRequest.townId())
        .orElseThrow(() -> new TownNotFoundException(TOWN_NOT_FOUND));
    List<AdjacentTownResponse> adjacentTowns = townRepository.findAdjacentTownsByPoint(
        town.getLongitude(), town.getLatitude(), adjacentTownRequest.rangeType().getDistance());
    return adjacentTowns.stream().map(AdjacentTownResponse::getTownId).toList();
  }

  public List<Long> findAdjacentTownWithRangeLevel(String name, String level) {
    Range range = rangeRepository.findAdjacentTownIds(name, level)
        .orElseThrow(() -> new TownNotFoundException(TOWN_NOT_FOUND));
    return range.getIds();
  }
}
