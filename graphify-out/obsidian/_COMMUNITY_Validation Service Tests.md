---
type: community
cohesion: 0.22
members: 17
---

# Validation Service Tests

**Cohesion:** 0.22 - loosely connected
**Members:** 17 nodes

## Members
- [[.buildFreightTrain()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.buildObstacle()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.buildObstacleNoEnd()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.buildPassengerTrain()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.buildSignal()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.buildTrack()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.setUp()_4]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_freightTrainInvalidTrack_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_multipleAnomalies_allReturned()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_obstacleInvalidTrack_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_obstacleNoEndTime_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_passengerTrainInvalidTrack_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_signalInvalidTrack_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_trackNoSignal_returnsAnomaly()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_unknownScenario_throws()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[.validate_validScenario_noAnomalies()]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java
- [[ValidationServiceTest]] - code - backend/src/test/java/fr/miniastra/application/service/ValidationServiceTest.java

## Live Query (requires Dataview plugin)

```dataview
TABLE source_file, type FROM #community/Validation_Service_Tests
SORT file.name ASC
```

## Connections to other communities
- 1 edge to [[_COMMUNITY_Train & Cargo Domain]]

## Top bridge nodes
- [[ValidationServiceTest]] - degree 17, connects to 1 community