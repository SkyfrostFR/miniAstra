package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.Electrification;
import fr.miniastra.domain.model.TrackSegment;

import java.util.List;
import java.util.UUID;

public record TrackSegmentResponse(
        UUID id,
        UUID scenarioId,
        String name,
        double startLat,
        double startLon,
        double endLat,
        double endLon,
        List<List<Double>> waypoints,
        double lengthM,
        int maxSpeedKmh,
        int trackCount,
        Electrification electrification,
        double gradePermil,
        UUID startStationId,
        UUID endStationId
) {
    public static TrackSegmentResponse from(TrackSegment t) {
        return new TrackSegmentResponse(t.id(), t.scenarioId(), t.name(),
                t.startLat(), t.startLon(), t.endLat(), t.endLon(),
                t.waypoints(), t.lengthM(), t.maxSpeedKmh(), t.trackCount(),
                t.electrification(), t.gradePermil(),
                t.startStationId(), t.endStationId());
    }
}
