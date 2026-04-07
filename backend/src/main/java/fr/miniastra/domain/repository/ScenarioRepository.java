package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.Scenario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioRepository {
    List<Scenario> findAll();
    Optional<Scenario> findById(UUID id);
    Optional<Scenario> findMostRecentlyUpdated();
    Scenario save(Scenario scenario);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
