package fr.miniastra.application.service;

import fr.miniastra.application.dto.ValidationAnomaly;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Valide les cohérences inter-objets d'un scénario et retourne la liste des anomalies.
 *
 * <p>Règles appliquées :
 * <ul>
 *   <li>TRAIN_INVALID_TRACK — train référençant un tronçon absent du scénario</li>
 *   <li>OBSTACLE_INVALID_TRACK — obstacle référençant un tronçon absent du scénario</li>
 *   <li>SIGNAL_INVALID_TRACK — signal référençant un tronçon absent du scénario</li>
 *   <li>OBSTACLE_NO_END_TIME — obstacle sans date de disparition (disappear_at_s null)</li>
 *   <li>TRACK_NO_SIGNAL — tronçon sans aucun signal associé</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackSegmentRepository;
    private final PassengerTrainRepository passengerTrainRepository;
    private final FreightTrainRepository freightTrainRepository;
    private final ObstacleRepository obstacleRepository;
    private final SignalRepository signalRepository;

    @Transactional(readOnly = true)
    public List<ValidationAnomaly> validate(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }

        List<TrackSegment> tracks = trackSegmentRepository.findAllByScenarioId(scenarioId);
        List<PassengerTrain> passengerTrains = passengerTrainRepository.findAllByScenarioId(scenarioId);
        List<FreightTrain> freightTrains = freightTrainRepository.findAllByScenarioId(scenarioId);
        List<Obstacle> obstacles = obstacleRepository.findAllByScenarioId(scenarioId);
        List<Signal> signals = signalRepository.findAllByScenarioId(scenarioId);

        Set<UUID> trackIds = tracks.stream().map(TrackSegment::id).collect(Collectors.toSet());
        Set<UUID> tracksWithSignal = signals.stream().map(Signal::trackId).collect(Collectors.toSet());

        List<ValidationAnomaly> anomalies = new ArrayList<>();

        for (PassengerTrain t : passengerTrains) {
            if (!trackIds.contains(t.trackId())) {
                anomalies.add(new ValidationAnomaly(
                        "train_passager", t.id(), t.name(),
                        "TRAIN_INVALID_TRACK",
                        "Le train passager \"" + t.name() + "\" référence un tronçon absent du scénario."
                ));
            }
        }

        for (FreightTrain t : freightTrains) {
            if (!trackIds.contains(t.trackId())) {
                anomalies.add(new ValidationAnomaly(
                        "train_fret", t.id(), t.name(),
                        "TRAIN_INVALID_TRACK",
                        "Le train fret \"" + t.name() + "\" référence un tronçon absent du scénario."
                ));
            }
        }

        for (Obstacle o : obstacles) {
            if (!trackIds.contains(o.trackId())) {
                anomalies.add(new ValidationAnomaly(
                        "obstacle", o.id(), o.name(),
                        "OBSTACLE_INVALID_TRACK",
                        "L'obstacle \"" + o.name() + "\" référence un tronçon absent du scénario."
                ));
            }
            if (o.disappearAtS() == null) {
                anomalies.add(new ValidationAnomaly(
                        "obstacle", o.id(), o.name(),
                        "OBSTACLE_NO_END_TIME",
                        "L'obstacle \"" + o.name() + "\" n'a pas de date de disparition (permanente)."
                ));
            }
        }

        for (Signal s : signals) {
            if (!trackIds.contains(s.trackId())) {
                anomalies.add(new ValidationAnomaly(
                        "signal", s.id(), s.name(),
                        "SIGNAL_INVALID_TRACK",
                        "Le signal \"" + s.name() + "\" référence un tronçon absent du scénario."
                ));
            }
        }

        for (TrackSegment t : tracks) {
            if (!tracksWithSignal.contains(t.id())) {
                anomalies.add(new ValidationAnomaly(
                        "tronçon", t.id(), t.name(),
                        "TRACK_NO_SIGNAL",
                        "Le tronçon \"" + t.name() + "\" n'a aucun signal associé."
                ));
            }
        }

        return anomalies;
    }
}
