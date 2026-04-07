package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.PassengerTrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PassengerTrainJpaRepository extends JpaRepository<PassengerTrainEntity, UUID> {
    List<PassengerTrainEntity> findAllByScenarioId(UUID scenarioId);
}
