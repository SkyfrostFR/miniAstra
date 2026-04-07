package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.TrackSegmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackSegmentJpaRepository extends JpaRepository<TrackSegmentEntity, UUID> {
    List<TrackSegmentEntity> findAllByScenarioId(UUID scenarioId);
    Optional<TrackSegmentEntity> findByScenarioIdAndName(UUID scenarioId, String name);
}
