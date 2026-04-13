---
type: community
cohesion: 0.06
members: 40
---

# Track Segment & Seeder

**Cohesion:** 0.06 - loosely connected
**Members:** 40 nodes

## Members
- [[.HaversineCalculator()]] - code - backend/src/main/java/fr/miniastra/domain/service/HaversineCalculator.java
- [[.create()]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[.delete()]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[.deleteById()_10]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.distanceM()]] - code - backend/src/main/java/fr/miniastra/domain/service/HaversineCalculator.java
- [[.existsById()_10]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.findAll()_1]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[.findAllByScenarioId()_16]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/TrackSegmentJpaRepository.java
- [[.findAllByScenarioId()_8]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.findById()_7]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[.findById()_17]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.findByScenarioIdAndName()_5]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/TrackSegmentJpaRepository.java
- [[.findByScenarioIdAndName()_3]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.run()]] - code - backend/src/main/java/fr/miniastra/infrastructure/seeder/FranceSimulationSeeder.java
- [[.save()_10]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[.seed()]] - code - backend/src/main/java/fr/miniastra/infrastructure/seeder/FranceSimulationSeeder.java
- [[.toDomain()_2]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/TrackSegmentEntityMapper.java
- [[.toEntity()_2]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/TrackSegmentEntityMapper.java
- [[.trackLengthM()]] - code - backend/src/main/java/fr/miniastra/domain/service/HaversineCalculator.java
- [[.update()]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[CreateTrackSegmentRequest.java]] - code - backend/src/main/java/fr/miniastra/api/dto/request/CreateTrackSegmentRequest.java
- [[Electrification.java]] - code - backend/src/main/java/fr/miniastra/domain/model/Electrification.java
- [[FranceSimulationSeeder]] - code - backend/src/main/java/fr/miniastra/infrastructure/seeder/FranceSimulationSeeder.java
- [[FranceSimulationSeeder.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/seeder/FranceSimulationSeeder.java
- [[HaversineCalculator]] - code - backend/src/main/java/fr/miniastra/domain/service/HaversineCalculator.java
- [[HaversineCalculator.java]] - code - backend/src/main/java/fr/miniastra/domain/service/HaversineCalculator.java
- [[TrackSegment.java]] - code - backend/src/main/java/fr/miniastra/domain/model/TrackSegment.java
- [[TrackSegmentController]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[TrackSegmentController.java]] - code - backend/src/main/java/fr/miniastra/api/controller/TrackSegmentController.java
- [[TrackSegmentEntity]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/entity/TrackSegmentEntity.java
- [[TrackSegmentEntity.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/entity/TrackSegmentEntity.java
- [[TrackSegmentEntityMapper]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/TrackSegmentEntityMapper.java
- [[TrackSegmentEntityMapper.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/mapper/TrackSegmentEntityMapper.java
- [[TrackSegmentJpaRepository]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/TrackSegmentJpaRepository.java
- [[TrackSegmentJpaRepository.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/repository/TrackSegmentJpaRepository.java
- [[TrackSegmentRepositoryAdapter]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[TrackSegmentRepositoryAdapter.java]] - code - backend/src/main/java/fr/miniastra/infrastructure/persistence/TrackSegmentRepositoryAdapter.java
- [[TrackSegmentResponse.java]] - code - backend/src/main/java/fr/miniastra/api/dto/response/TrackSegmentResponse.java
- [[TrackSegmentService.java]] - code - backend/src/main/java/fr/miniastra/application/service/TrackSegmentService.java
- [[from()_5]] - code - backend/src/main/java/fr/miniastra/api/dto/response/TrackSegmentResponse.java

## Live Query (requires Dataview plugin)

```dataview
TABLE source_file, type FROM #community/Track_Segment_&_Seeder
SORT file.name ASC
```

## Connections to other communities
- 14 edges to [[_COMMUNITY_Train & Cargo Domain]]
- 4 edges to [[_COMMUNITY_Station & Conflict Management]]
- 2 edges to [[_COMMUNITY_Scenario & Excel Export]]
- 1 edge to [[_COMMUNITY_Passenger Train & GeoJSON]]
- 1 edge to [[_COMMUNITY_Track Segment Service]]

## Top bridge nodes
- [[FranceSimulationSeeder.java]] - degree 11, connects to 3 communities
- [[TrackSegmentService.java]] - degree 9, connects to 3 communities
- [[TrackSegmentController.java]] - degree 6, connects to 2 communities
- [[TrackSegment.java]] - degree 11, connects to 1 community
- [[Electrification.java]] - degree 7, connects to 1 community