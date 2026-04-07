package fr.miniastra.application.service;

import fr.miniastra.application.dto.ValidationAnomaly;
import fr.miniastra.domain.model.CargoType;
import fr.miniastra.domain.model.Electrification;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.ObstacleType;
import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.SignalDirection;
import fr.miniastra.domain.model.SignalState;
import fr.miniastra.domain.model.SignalType;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock private ScenarioRepository scenarioRepository;
    @Mock private TrackSegmentRepository trackSegmentRepository;
    @Mock private PassengerTrainRepository passengerTrainRepository;
    @Mock private FreightTrainRepository freightTrainRepository;
    @Mock private ObstacleRepository obstacleRepository;
    @Mock private SignalRepository signalRepository;

    private ValidationService service;

    private final UUID scenarioId = UUID.randomUUID();
    private final UUID trackId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ValidationService(scenarioRepository, trackSegmentRepository,
                passengerTrainRepository, freightTrainRepository,
                obstacleRepository, signalRepository);
    }

    @Test
    @DisplayName("scénario inconnu → exception")
    void validate_unknownScenario_throws() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(false);

        assertThatThrownBy(() -> service.validate(scenarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(scenarioId.toString());
    }

    @Test
    @DisplayName("scénario valide → aucune anomalie")
    void validate_validScenario_noAnomalies() {
        TrackSegment track = buildTrack(trackId);
        Signal signal = buildSignal(trackId);

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(track));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(signal));

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).isEmpty();
    }

    @Test
    @DisplayName("train passager sur tronçon inexistant → TRAIN_INVALID_TRACK")
    void validate_passengerTrainInvalidTrack_returnsAnomaly() {
        PassengerTrain train = buildPassengerTrain(UUID.randomUUID()); // unknown trackId

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(train));
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).hasSize(1);
        assertThat(anomalies.get(0).rule()).isEqualTo("TRAIN_INVALID_TRACK");
    }

    @Test
    @DisplayName("train fret sur tronçon inexistant → TRAIN_INVALID_TRACK")
    void validate_freightTrainInvalidTrack_returnsAnomaly() {
        FreightTrain train = buildFreightTrain(UUID.randomUUID());

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(train));
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).hasSize(1);
        assertThat(anomalies.get(0).rule()).isEqualTo("TRAIN_INVALID_TRACK");
    }

    @Test
    @DisplayName("obstacle sur tronçon inexistant → OBSTACLE_INVALID_TRACK")
    void validate_obstacleInvalidTrack_returnsAnomaly() {
        Obstacle obstacle = buildObstacle(UUID.randomUUID(), 30);

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(obstacle));
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).extracting(ValidationAnomaly::rule)
                .contains("OBSTACLE_INVALID_TRACK");
    }

    @Test
    @DisplayName("obstacle sans heure de fin → OBSTACLE_NO_END_TIME")
    void validate_obstacleNoEndTime_returnsAnomaly() {
        TrackSegment track = buildTrack(trackId);
        Obstacle obstacle = buildObstacleNoEnd(trackId);
        Signal signal = buildSignal(trackId);

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(track));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(obstacle));
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(signal));

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).hasSize(1);
        assertThat(anomalies.get(0).rule()).isEqualTo("OBSTACLE_NO_END_TIME");
    }

    @Test
    @DisplayName("signal sur tronçon inexistant → SIGNAL_INVALID_TRACK")
    void validate_signalInvalidTrack_returnsAnomaly() {
        TrackSegment track = buildTrack(trackId);
        Signal signal = buildSignal(UUID.randomUUID()); // unknown trackId

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(track));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(signal));

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).extracting(ValidationAnomaly::rule)
                .contains("SIGNAL_INVALID_TRACK", "TRACK_NO_SIGNAL");
    }

    @Test
    @DisplayName("tronçon sans signal → TRACK_NO_SIGNAL")
    void validate_trackNoSignal_returnsAnomaly() {
        TrackSegment track = buildTrack(trackId);

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(track));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).hasSize(1);
        assertThat(anomalies.get(0).rule()).isEqualTo("TRACK_NO_SIGNAL");
    }

    @Test
    @DisplayName("plusieurs anomalies → toutes retournées")
    void validate_multipleAnomalies_allReturned() {
        PassengerTrain train = buildPassengerTrain(UUID.randomUUID());
        Obstacle obstacle = buildObstacleNoEnd(UUID.randomUUID());

        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(train));
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of(obstacle));
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        List<ValidationAnomaly> anomalies = service.validate(scenarioId);

        assertThat(anomalies).hasSizeGreaterThanOrEqualTo(2);
    }

    // ── Builders ─────────────────────────────────────────────────────────────

    private TrackSegment buildTrack(UUID id) {
        return new TrackSegment(id, scenarioId, "Voie A",
                48.8, 2.3, 48.9, 2.4, List.of(),
                5000, 120, 1, Electrification.NONE, 0.0);
    }

    private PassengerTrain buildPassengerTrain(UUID trackRef) {
        return new PassengerTrain(UUID.randomUUID(), scenarioId, "TGV 001",
                "TGV_DUPLEX", trackRef, 0.0, TrainDirection.PAIR, 0.0, 200, "TGV001");
    }

    private FreightTrain buildFreightTrain(UUID trackRef) {
        return new FreightTrain(UUID.randomUUID(), scenarioId, "Fret 001",
                "BB_27000", trackRef, 0.0, TrainDirection.PAIR, 0.0, 500.0, CargoType.VIDE);
    }

    private Obstacle buildObstacle(UUID trackRef, Integer disappearAtS) {
        return new Obstacle(UUID.randomUUID(), scenarioId, "Chantier",
                ObstacleType.CHANTIER, trackRef, 100.0, 50.0, true, 30, 200.0, 0, disappearAtS);
    }

    private Obstacle buildObstacleNoEnd(UUID trackRef) {
        return buildObstacle(trackRef, null);
    }

    private Signal buildSignal(UUID trackRef) {
        return new Signal(UUID.randomUUID(), scenarioId, "Signal A",
                SignalType.CARRE, trackRef, 200.0, SignalDirection.PAIR, SignalState.VOIE_LIBRE);
    }
}
