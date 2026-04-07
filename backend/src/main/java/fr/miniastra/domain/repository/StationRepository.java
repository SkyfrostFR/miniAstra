package fr.miniastra.domain.repository;

import fr.miniastra.domain.model.Station;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationRepository {
    List<Station> findAllByScenarioId(UUID scenarioId);
    Optional<Station> findById(UUID id);
    Optional<Station> findByScenarioIdAndName(UUID scenarioId, String name);
    Station save(Station station);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
