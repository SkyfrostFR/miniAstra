package fr.miniastra.application.service;

import fr.miniastra.application.dto.ScenarioStats;
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
class ScenarioStatsServiceTest {

    @Mock private ScenarioRepository scenarioRepository;
    @Mock private TrackSegmentRepository trackSegmentRepository;
    @Mock private PassengerTrainRepository passengerTrainRepository;
    @Mock private FreightTrainRepository freightTrainRepository;
    @Mock private ObstacleRepository obstacleRepository;
    @Mock private SignalRepository signalRepository;

    private ScenarioStatsService service;

    private final UUID scenarioId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ScenarioStatsService(scenarioRepository, trackSegmentRepository,
                passengerTrainRepository, freightTrainRepository,
                obstacleRepository, signalRepository);
    }

    @Test
    @DisplayName("scénario inconnu → exception")
    void getStats_unknownScenario_throws() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(false);

        assertThatThrownBy(() -> service.getStats(scenarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(scenarioId.toString());
    }

    @Test
    @DisplayName("scénario vide → tous les compteurs à 0")
    void getStats_emptyScenario_allZero() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        ScenarioStats stats = service.getStats(scenarioId);

        assertThat(stats.trackSegments()).isZero();
        assertThat(stats.passengerTrains()).isZero();
        assertThat(stats.freightTrains()).isZero();
        assertThat(stats.obstacles()).isZero();
        assertThat(stats.signals()).isZero();
    }

    @Test
    @DisplayName("comptage correct des tronçons")
    void getStats_trackSegments_correctCount() {
        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildTrack(), buildTrack(), buildTrack()));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        ScenarioStats stats = service.getStats(scenarioId);

        assertThat(stats.trackSegments()).isEqualTo(3);
    }

    @Test
    @DisplayName("comptage correct des trains voyageurs")
    void getStats_passengerTrains_correctCount() {
        UUID trackId = UUID.randomUUID();
        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(passengerTrainRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildPassengerTrain(trackId), buildPassengerTrain(trackId)));
        when(freightTrainRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(obstacleRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());
        when(signalRepository.findAllByScenarioId(scenarioId)).thenReturn(List.of());

        ScenarioStats stats = service.getStats(scenarioId);

        assertThat(stats.passengerTrains()).isEqualTo(2);
    }

    @Test
    @DisplayName("comptage complet — tous les types présents")
    void getStats_allTypes_correctCounts() {
        UUID trackId = UUID.randomUUID();
        when(scenarioRepository.existsById(scenarioId)).thenReturn(true);
        when(trackSegmentRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildTrack()));
        when(passengerTrainRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildPassengerTrain(trackId)));
        when(freightTrainRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildFreightTrain(trackId), buildFreightTrain(trackId)));
        when(obstacleRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildObstacle(trackId)));
        when(signalRepository.findAllByScenarioId(scenarioId))
                .thenReturn(List.of(buildSignal(trackId), buildSignal(trackId), buildSignal(trackId)));

        ScenarioStats stats = service.getStats(scenarioId);

        assertThat(stats.trackSegments()).isEqualTo(1);
        assertThat(stats.passengerTrains()).isEqualTo(1);
        assertThat(stats.freightTrains()).isEqualTo(2);
        assertThat(stats.obstacles()).isEqualTo(1);
        assertThat(stats.signals()).isEqualTo(3);
    }

    // ── Builders ─────────────────────────────────────────────────────────────

    private TrackSegment buildTrack() {
        return new TrackSegment(UUID.randomUUID(), scenarioId, "Voie",
                48.8, 2.3, 48.9, 2.4, List.of(),
                5000, 120, 1, Electrification.NONE, 0.0);
    }

    private PassengerTrain buildPassengerTrain(UUID trackRef) {
        return new PassengerTrain(UUID.randomUUID(), scenarioId, "TGV",
                "TGV_DUPLEX", trackRef, 0.0, TrainDirection.PAIR, 0.0, 200, null);
    }

    private FreightTrain buildFreightTrain(UUID trackRef) {
        return new FreightTrain(UUID.randomUUID(), scenarioId, "Fret",
                "BB_27000", trackRef, 0.0, TrainDirection.PAIR, 0.0, 500.0, CargoType.VIDE);
    }

    private Obstacle buildObstacle(UUID trackRef) {
        return new Obstacle(UUID.randomUUID(), scenarioId, "Chantier",
                ObstacleType.CHANTIER, trackRef, 0.0, 10.0, false, 30, 200.0, 0, 3600);
    }

    private Signal buildSignal(UUID trackRef) {
        return new Signal(UUID.randomUUID(), scenarioId, "Signal",
                SignalType.CARRE, trackRef, 0.0, SignalDirection.PAIR, SignalState.VOIE_LIBRE);
    }
}
