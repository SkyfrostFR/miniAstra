package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.PassengerTrain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerTrainRepository {
    List<PassengerTrain> findAllByScenarioId(UUID scenarioId);
    Optional<PassengerTrain> findById(UUID id);
    PassengerTrain save(PassengerTrain train);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
