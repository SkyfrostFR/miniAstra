package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.ObstacleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ObstacleJpaRepository extends JpaRepository<ObstacleEntity, UUID> {
    List<ObstacleEntity> findAllByScenarioId(UUID scenarioId);
}
