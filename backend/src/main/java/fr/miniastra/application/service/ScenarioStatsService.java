package fr.miniastra.application.service;

import fr.miniastra.application.dto.ScenarioStats;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScenarioStatsService {

    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackSegmentRepository;
    private final PassengerTrainRepository passengerTrainRepository;
    private final FreightTrainRepository freightTrainRepository;
    private final ObstacleRepository obstacleRepository;
    private final SignalRepository signalRepository;

    @Transactional(readOnly = true)
    public ScenarioStats getStats(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }
        return new ScenarioStats(
                trackSegmentRepository.findAllByScenarioId(scenarioId).size(),
                passengerTrainRepository.findAllByScenarioId(scenarioId).size(),
                freightTrainRepository.findAllByScenarioId(scenarioId).size(),
                obstacleRepository.findAllByScenarioId(scenarioId).size(),
                signalRepository.findAllByScenarioId(scenarioId).size()
        );
    }
}
