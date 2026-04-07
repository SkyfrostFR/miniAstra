package fr.miniastra.application.service;

import fr.miniastra.domain.model.CargoType;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreightTrainService {

    private final FreightTrainRepository repository;
    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackRepository;

    @Transactional(readOnly = true)
    public List<FreightTrain> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public FreightTrain findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Train fret introuvable : id=" + id));
    }

    @Transactional
    public FreightTrain create(UUID scenarioId, String name, String modelCode,
                                UUID trackId, double positionM, TrainDirection direction,
                                double initialSpeedKmh, double loadT, CargoType cargoType) {
        requireScenario(scenarioId);
        requireTrack(trackId);
        var train = new FreightTrain(null, scenarioId, name, modelCode, trackId, positionM,
                direction, initialSpeedKmh, loadT, cargoType);
        return repository.save(train);
    }

    @Transactional
    public FreightTrain update(UUID scenarioId, UUID id, String name, String modelCode,
                                UUID trackId, double positionM, TrainDirection direction,
                                double initialSpeedKmh, double loadT, CargoType cargoType) {
        findById(id);
        requireTrack(trackId);
        var train = new FreightTrain(id, scenarioId, name, modelCode, trackId, positionM,
                direction, initialSpeedKmh, loadT, cargoType);
        return repository.save(train);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Train fret introuvable : id=" + id);
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
