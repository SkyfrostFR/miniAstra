package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationJpaRepository extends JpaRepository<StationEntity, UUID> {
    List<StationEntity> findAllByScenarioId(UUID scenarioId);
    Optional<StationEntity> findByScenarioIdAndName(UUID scenarioId, String name);
}
