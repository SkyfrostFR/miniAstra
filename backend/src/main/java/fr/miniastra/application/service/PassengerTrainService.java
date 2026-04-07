package fr.miniastra.application.service;

import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerTrainService {

    private final PassengerTrainRepository repository;
    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackRepository;

    @Transactional(readOnly = true)
    public List<PassengerTrain> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public PassengerTrain findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Train passager introuvable : id=" + id));
    }

    @Transactional
    public PassengerTrain create(UUID scenarioId, String name, String modelCode,
                                  UUID trackId, double positionM, TrainDirection direction,
                                  double initialSpeedKmh, int passengerCount, String serviceNumber) {
        requireScenario(scenarioId);
        requireTrack(trackId);
        var train = new PassengerTrain(null, scenarioId, name, modelCode, trackId, positionM,
                direction, initialSpeedKmh, passengerCount, serviceNumber);
        return repository.save(train);
    }

    @Transactional
    public PassengerTrain update(UUID scenarioId, UUID id, String name, String modelCode,
                                  UUID trackId, double positionM, TrainDirection direction,
                                  double initialSpeedKmh, int passengerCount, String serviceNumber) {
        findById(id);
        requireTrack(trackId);
        var train = new PassengerTrain(id, scenarioId, name, modelCode, trackId, positionM,
                direction, initialSpeedKmh, passengerCount, serviceNumber);
        return repository.save(train);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Train passager introuvable : id=" + id);
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
