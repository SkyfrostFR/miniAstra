package fr.miniastra.application.service;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.ObstacleType;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObstacleService {

    private final ObstacleRepository repository;
    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackRepository;

    @Transactional(readOnly = true)
    public List<Obstacle> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public Obstacle findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Obstacle introuvable : id=" + id));
    }

    @Transactional
    public Obstacle create(UUID scenarioId, String name, ObstacleType type, UUID trackId,
                            double positionM, double lengthM, boolean blocking,
                            int speedLimitKmh, double visibilityM,
                            int appearAtS, Integer disappearAtS) {
        requireScenario(scenarioId);
        requireTrack(trackId);
        var obstacle = new Obstacle(null, scenarioId, name, type, trackId, positionM, lengthM,
                blocking, speedLimitKmh, visibilityM, appearAtS, disappearAtS);
        return repository.save(obstacle);
    }

    @Transactional
    public Obstacle update(UUID scenarioId, UUID id, String name, ObstacleType type, UUID trackId,
                            double positionM, double lengthM, boolean blocking,
                            int speedLimitKmh, double visibilityM,
                            int appearAtS, Integer disappearAtS) {
        findById(id);
        requireTrack(trackId);
        var obstacle = new Obstacle(id, scenarioId, name, type, trackId, positionM, lengthM,
                blocking, speedLimitKmh, visibilityM, appearAtS, disappearAtS);
        return repository.save(obstacle);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Obstacle introuvable : id=" + id);
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
