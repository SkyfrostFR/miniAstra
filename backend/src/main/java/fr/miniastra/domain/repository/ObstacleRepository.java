package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.Obstacle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObstacleRepository {
    List<Obstacle> findAllByScenarioId(UUID scenarioId);
    Optional<Obstacle> findById(UUID id);
    Obstacle save(Obstacle obstacle);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
