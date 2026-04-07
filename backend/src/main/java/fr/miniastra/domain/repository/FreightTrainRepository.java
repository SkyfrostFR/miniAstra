package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.FreightTrain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FreightTrainRepository {
    List<FreightTrain> findAllByScenarioId(UUID scenarioId);
    Optional<FreightTrain> findById(UUID id);
    FreightTrain save(FreightTrain train);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
