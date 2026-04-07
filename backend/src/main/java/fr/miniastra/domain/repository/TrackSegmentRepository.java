package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.TrackSegment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackSegmentRepository {
    List<TrackSegment> findAllByScenarioId(UUID scenarioId);
    Optional<TrackSegment> findById(UUID id);
    Optional<TrackSegment> findByScenarioIdAndName(UUID scenarioId, String name);
    TrackSegment save(TrackSegment track);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
