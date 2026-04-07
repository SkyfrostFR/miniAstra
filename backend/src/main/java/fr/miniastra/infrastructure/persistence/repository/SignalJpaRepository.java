package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.SignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SignalJpaRepository extends JpaRepository<SignalEntity, UUID> {
    List<SignalEntity> findAllByScenarioId(UUID scenarioId);
}
