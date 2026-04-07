package fr.miniastra.application.service;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.Station;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.domain.repository.StationRepository;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeoJsonService {

    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentRepository trackSegmentRepository;
    private final PassengerTrainRepository passengerTrainRepository;
    private final FreightTrainRepository freightTrainRepository;
    private final ObstacleRepository obstacleRepository;
    private final SignalRepository signalRepository;
    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> buildFeatureCollection(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }

        List<Map<String, Object>> features = new ArrayList<>();
        List<TrackSegment> tracks = trackSegmentRepository.findAllByScenarioId(scenarioId);

        // Build a map for fast track lookup
        Map<UUID, TrackSegment> trackById = new HashMap<>();
        for (TrackSegment t : tracks) {
            trackById.put(t.id(), t);
        }

        // Stations → Point
        for (Station station : stationRepository.findAllByScenarioId(scenarioId)) {
            Map<String, Object> props = new HashMap<>();
            props.put("objectType", "STATION");
            props.put("id", station.id().toString());
            props.put("name", station.name());
            features.add(buildPointFeature(station.lon(), station.lat(), props));
        }

        // Track segments → LineString
        for (TrackSegment t : tracks) {
            features.add(buildLineStringFeature(t));
        }

        // Passenger trains → Point
        for (PassengerTrain train : passengerTrainRepository.findAllByScenarioId(scenarioId)) {
            TrackSegment track = trackById.get(train.trackId());
            if (track != null) {
                double[] coords = interpolate(track, train.positionM());
                Map<String, Object> props = new HashMap<>();
                props.put("objectType", "PASSENGER_TRAIN");
                props.put("id", train.id().toString());
                props.put("name", train.name());
                props.put("modelCode", train.modelCode());
                props.put("direction", train.direction().name());
                features.add(buildPointFeature(coords[0], coords[1], props));
            }
        }

        // Freight trains → Point
        for (FreightTrain train : freightTrainRepository.findAllByScenarioId(scenarioId)) {
            TrackSegment track = trackById.get(train.trackId());
            if (track != null) {
                double[] coords = interpolate(track, train.positionM());
                Map<String, Object> props = new HashMap<>();
                props.put("objectType", "FREIGHT_TRAIN");
                props.put("id", train.id().toString());
                props.put("name", train.name());
                props.put("cargoType", train.cargoType().name());
                features.add(buildPointFeature(coords[0], coords[1], props));
            }
        }

        // Obstacles → Point
        for (Obstacle obstacle : obstacleRepository.findAllByScenarioId(scenarioId)) {
            TrackSegment track = trackById.get(obstacle.trackId());
            if (track != null) {
                double[] coords = interpolate(track, obstacle.positionM());
                Map<String, Object> props = new HashMap<>();
                props.put("objectType", "OBSTACLE");
                props.put("id", obstacle.id().toString());
                props.put("name", obstacle.name());
                props.put("type", obstacle.type().name());
                props.put("blocking", obstacle.blocking());
                features.add(buildPointFeature(coords[0], coords[1], props));
            }
        }

        // Signals → Point
        for (Signal signal : signalRepository.findAllByScenarioId(scenarioId)) {
            TrackSegment track = trackById.get(signal.trackId());
            if (track != null) {
                double[] coords = interpolate(track, signal.positionM());
                Map<String, Object> props = new HashMap<>();
                props.put("objectType", "SIGNAL");
                props.put("id", signal.id().toString());
                props.put("name", signal.name());
                props.put("type", signal.type().name());
                props.put("state", signal.initialState().name());
                features.add(buildPointFeature(coords[0], coords[1], props));
            }
        }

        Map<String, Object> collection = new HashMap<>();
        collection.put("type", "FeatureCollection");
        collection.put("features", features);
        return collection;
    }

    private Map<String, Object> buildLineStringFeature(TrackSegment t) {
        List<List<Double>> coordinates = new ArrayList<>();
        coordinates.add(List.of(t.startLon(), t.startLat()));
        for (List<Double> wp : t.waypoints()) {
            if (wp.size() >= 2) {
                coordinates.add(List.of(wp.get(1), wp.get(0))); // [lon, lat]
            }
        }
        coordinates.add(List.of(t.endLon(), t.endLat()));

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "LineString");
        geometry.put("coordinates", coordinates);

        Map<String, Object> props = new HashMap<>();
        props.put("objectType", "TRACK_SEGMENT");
        props.put("id", t.id().toString());
        props.put("name", t.name());
        props.put("maxSpeedKmh", t.maxSpeedKmh());
        props.put("lengthM", t.lengthM());
        props.put("electrification", t.electrification().name());

        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", props);
        return feature;
    }

    private Map<String, Object> buildPointFeature(double lon, double lat, Map<String, Object> props) {
        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", List.of(lon, lat));

        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", props);
        return feature;
    }

    /**
     * Interpolates a lat/lon position along the track polyline at positionM meters from start.
     * Returns [lon, lat].
     */
    private double[] interpolate(TrackSegment t, double positionM) {
        // Build ordered list of [lat, lon] points
        List<double[]> points = new ArrayList<>();
        points.add(new double[]{t.startLat(), t.startLon()});
        for (List<Double> wp : t.waypoints()) {
            if (wp.size() >= 2) {
                points.add(new double[]{wp.get(0), wp.get(1)});
            }
        }
        points.add(new double[]{t.endLat(), t.endLon()});

        double remaining = positionM;
        for (int i = 0; i < points.size() - 1; i++) {
            double[] a = points.get(i);
            double[] b = points.get(i + 1);
            double segLen = haversineM(a[0], a[1], b[0], b[1]);
            if (remaining <= segLen || i == points.size() - 2) {
                double frac = segLen > 0 ? Math.min(remaining / segLen, 1.0) : 0.0;
                double lat = a[0] + frac * (b[0] - a[0]);
                double lon = a[1] + frac * (b[1] - a[1]);
                return new double[]{lon, lat};
            }
            remaining -= segLen;
        }
        // Fallback: end of track
        double[] last = points.get(points.size() - 1);
        return new double[]{last[1], last[0]};
    }

    private double haversineM(double lat1, double lon1, double lat2, double lon2) {
        double R = 6_371_000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
