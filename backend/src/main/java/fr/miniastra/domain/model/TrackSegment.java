package fr.miniastra.domain.model;

import java.util.List;
import java.util.UUID;

public record TrackSegment(
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
) {}
