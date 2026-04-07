package fr.miniastra.application.service;

import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.SignalDirection;
import fr.miniastra.domain.model.SignalState;
import fr.miniastra.domain.model.SignalType;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository repository;
    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackRepository;

    @Transactional(readOnly = true)
    public List<Signal> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public Signal findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Signal introuvable : id=" + id));
    }

    @Transactional
    public Signal create(UUID scenarioId, String name, SignalType type, UUID trackId,
                          double positionM, SignalDirection direction, SignalState initialState) {
        requireScenario(scenarioId);
        requireTrack(trackId);
        var signal = new Signal(null, scenarioId, name, type, trackId, positionM, direction, initialState);
        return repository.save(signal);
    }

    @Transactional
    public Signal update(UUID scenarioId, UUID id, String name, SignalType type, UUID trackId,
                          double positionM, SignalDirection direction, SignalState initialState) {
        findById(id);
        requireTrack(trackId);
        var signal = new Signal(id, scenarioId, name, type, trackId, positionM, direction, initialState);
        return repository.save(signal);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Signal introuvable : id=" + id);
        }
        repository.deleteById(id);
    }

    private void requireScenario(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }
    }

    private void requireTrack(UUID trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new IllegalArgumentException("Tronçon introuvable : id=" + trackId);
        }
    }
}
