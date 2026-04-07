package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.Signal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SignalRepository {
    List<Signal> findAllByScenarioId(UUID scenarioId);
    Optional<Signal> findById(UUID id);
    Signal save(Signal signal);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
