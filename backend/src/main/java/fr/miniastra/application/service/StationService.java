package fr.miniastra.application.service;

import fr.miniastra.domain.model.Station;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository repository;
    private final ScenarioRepository scenarioRepository;

    @Transactional(readOnly = true)
    public List<Station> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public Station findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gare introuvable : id=" + id));
    }

    @Transactional
    public Station create(UUID scenarioId, String name, double lat, double lon) {
        requireScenario(scenarioId);
        checkNameUnique(scenarioId, name, null);
        return repository.save(new Station(null, scenarioId, name, lat, lon));
    }

    @Transactional
    public Station update(UUID scenarioId, UUID id, String name, double lat, double lon) {
        findById(id);
        checkNameUnique(scenarioId, name, id);
        return repository.save(new Station(id, scenarioId, name, lat, lon));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Gare introuvable : id=" + id);
        }
        repository.deleteById(id);
    }

    private void requireScenario(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }
    }

    private void checkNameUnique(UUID scenarioId, String name, UUID excludeId) {
        repository.findByScenarioIdAndName(scenarioId, name).ifPresent(existing -> {
            if (!existing.id().equals(excludeId)) {
                throw new IllegalArgumentException(
                        "Une gare nommée \"" + name + "\" existe déjà dans ce scénario.");
            }
        });
    }
}
