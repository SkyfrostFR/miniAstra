package fr.miniastra.infrastructure.persistence.repository;

import fr.miniastra.infrastructure.persistence.entity.ScenarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScenarioJpaRepository extends JpaRepository<ScenarioEntity, UUID> {
    Optional<ScenarioEntity> findFirstByOrderByUpdatedAtDesc();
}
