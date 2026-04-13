---
type: community
cohesion: 0.05
members: 43
---

# Scenario & Excel Export

**Cohesion:** 0.05 - loosely connected
**Members:** 43 nodes

## Members
- [[.create()_3]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.create()_13]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[.delete()_3]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.delete()_13]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[.deleteById()_9]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.existsById()_9]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.export()]] - code - backend/src/main/java/fr/miniastra/api/controller/ExcelExportController.java
- [[.findAll()_4]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.findAll()_8]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.findAll()_9]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[.findById()_10]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.findById()_16]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.findById()_27]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[.findFirstByOrderByUpdatedAtDesc()]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/ScenarioJpaRepository.java
- [[.findLastUsed()]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.findMostRecentlyUpdated()_1]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.findMostRecentlyUpdated()_2]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[.getStats()]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.save()_9]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[.toDomain()_6]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/ScenarioEntityMapper.java
- [[.toEntity()_6]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/ScenarioEntityMapper.java
- [[.update()_3]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[.update()_13]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[CreateScenarioRequest.java]] - code - backend/src/main/java/fr/miniastra/api/dto/request/CreateScenarioRequest.java
- [[ExcelExportController]] - code - backend/src/main/java/fr/miniastra/api/controller/ExcelExportController.java
- [[ExcelExportController.java]] - code - backend/src/main/java/fr/miniastra/api/controller/ExcelExportController.java
- [[Scenario.java]] - code - backend/src/main/java/fr/miniastra/domain/model/Scenario.java
- [[ScenarioController]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[ScenarioController.java]] - code - backend/src/main/java/fr/miniastra/api/controller/ScenarioController.java
- [[ScenarioEntity]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/entity/ScenarioEntity.java
- [[ScenarioEntity.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/entity/ScenarioEntity.java
- [[ScenarioEntityMapper]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/ScenarioEntityMapper.java
- [[ScenarioEntityMapper.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/ScenarioEntityMapper.java
- [[ScenarioJpaRepository]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/ScenarioJpaRepository.java
- [[ScenarioJpaRepository.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/ScenarioJpaRepository.java
- [[ScenarioRepositoryAdapter]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[ScenarioRepositoryAdapter.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/ScenarioRepositoryAdapter.java
- [[ScenarioResponse.java]] - code - backend/src/main/java/fr/miniastra/api/dto/response/ScenarioResponse.java
- [[ScenarioService]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[ScenarioService.java]] - code - backend/src/main/java/fr/miniastra/application/service/ScenarioService.java
- [[ScenarioStatsResponse.java]] - code - backend/src/main/java/fr/miniastra/api/dto/response/ScenarioStatsResponse.java
- [[from()]] - code - backend/src/main/java/fr/miniastra/api/dto/response/ScenarioResponse.java
- [[from()_2]] - code - backend/src/main/java/fr/miniastra/api/dto/response/ScenarioStatsResponse.java

## Live Query (requires Dataview plugin)

```dataview
TABLE source_file, type FROM #community/Scenario_&_Excel_Export
SORT file.name ASC
```

## Connections to other communities
- 6 edges to [[_COMMUNITY_Train & Cargo Domain]]
- 2 edges to [[_COMMUNITY_Track Segment & Seeder]]
- 2 edges to [[_COMMUNITY_Passenger Train & GeoJSON]]

## Top bridge nodes
- [[ScenarioController.java]] - degree 7, connects to 2 communities
- [[Scenario.java]] - degree 6, connects to 2 communities
- [[ScenarioService.java]] - degree 6, connects to 2 communities
- [[ExcelExportController.java]] - degree 4, connects to 2 communities
- [[ScenarioRepositoryAdapter.java]] - degree 5, connects to 1 community