package fr.miniastra.application.service;

import fr.miniastra.domain.model.Scenario;
import fr.miniastra.domain.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository repository;

    @Transactional(readOnly = true)
    public List<Scenario> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Scenario findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scénario introuvable : id=" + id));
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Scenario> findMostRecentlyUpdated() {
        return repository.findMostRecentlyUpdated();
    }

    @Transactional
    public Scenario create(String name, String description, int durationS, LocalTime startTime) {
        var scenario = new Scenario(null, name, description, durationS, startTime,
                LocalDateTime.now(), LocalDateTime.now());
        return repository.save(scenario);
    }

    @Transactional
    public Scenario update(UUID id, String name, String description, int durationS, LocalTime startTime) {
        var existing = findById(id);
        var updated = new Scenario(
                existing.id(), name, description, durationS, startTime,
                existing.createdAt(), LocalDateTime.now());
        return repository.save(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + id);
        }
        repository.deleteById(id);
    }
}
