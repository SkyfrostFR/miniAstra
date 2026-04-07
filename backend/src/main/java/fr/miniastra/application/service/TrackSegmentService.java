package fr.miniastra.application.service;

import fr.miniastra.domain.model.Electrification;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.StationRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import fr.miniastra.domain.service.HaversineCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackSegmentService {

    private final TrackSegmentRepository repository;
    private final ScenarioRepository scenarioRepository;
    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public List<TrackSegment> findAllByScenarioId(UUID scenarioId) {
        requireScenario(scenarioId);
        return repository.findAllByScenarioId(scenarioId);
    }

    @Transactional(readOnly = true)
    public TrackSegment findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tronçon introuvable : id=" + id));
    }

    @Transactional
    public TrackSegment create(UUID scenarioId, String name, double startLat, double startLon,
                                double endLat, double endLon, List<List<Double>> waypoints,
                                int maxSpeedKmh, int trackCount, Electrification electrification,
                                double gradePermil, UUID startStationId, UUID endStationId) {
        requireScenario(scenarioId);
        checkNameUnique(scenarioId, name, null);
        if (startStationId != null) requireStation(startStationId);
        if (endStationId != null) requireStation(endStationId);

        double lengthM = HaversineCalculator.trackLengthM(startLat, startLon, waypoints, endLat, endLon);
        var track = new TrackSegment(null, scenarioId, name, startLat, startLon, endLat, endLon,
                waypoints, lengthM, maxSpeedKmh, trackCount, electrification, gradePermil,
                startStationId, endStationId);
        return repository.save(track);
    }

    @Transactional
    public TrackSegment update(UUID scenarioId, UUID id, String name, double startLat, double startLon,
                                double endLat, double endLon, List<List<Double>> waypoints,
                                int maxSpeedKmh, int trackCount, Electrification electrification,
                                double gradePermil, UUID startStationId, UUID endStationId) {
        findById(id);
        checkNameUnique(scenarioId, name, id);
        if (startStationId != null) requireStation(startStationId);
        if (endStationId != null) requireStation(endStationId);

        double lengthM = HaversineCalculator.trackLengthM(startLat, startLon, waypoints, endLat, endLon);
        var track = new TrackSegment(id, scenarioId, name, startLat, startLon, endLat, endLon,
                waypoints, lengthM, maxSpeedKmh, trackCount, electrification, gradePermil,
                startStationId, endStationId);
        return repository.save(track);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Tronçon introuvable : id=" + id);
        }
        repository.deleteById(id);
    }

    private void requireScenario(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }
    }

    private void requireStation(UUID stationId) {
        if (!stationRepository.existsById(stationId)) {
            throw new IllegalArgumentException("Gare introuvable : id=" + stationId);
        }
    }

    private void checkNameUnique(UUID scenarioId, String name, UUID excludeId) {
        repository.findByScenarioIdAndName(scenarioId, name).ifPresent(existing -> {
            if (!existing.id().equals(excludeId)) {
                throw new IllegalArgumentException(
                        "Un tronçon nommé \"" + name + "\" existe déjà dans ce scénario.");
            }
        });
    }
}
