package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.FreightTrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FreightTrainJpaRepository extends JpaRepository<FreightTrainEntity, UUID> {
    List<FreightTrainEntity> findAllByScenarioId(UUID scenarioId);
}
