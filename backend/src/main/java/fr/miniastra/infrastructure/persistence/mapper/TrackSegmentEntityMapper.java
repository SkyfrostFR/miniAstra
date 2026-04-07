package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.Electrification;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.infrastructure.persistence.entity.TrackSegmentEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TrackSegmentEntityMapper {

    public TrackSegment toDomain(TrackSegmentEntity e) {
        return new TrackSegment(
                e.getId(),
                e.getScenarioId(),
                e.getName(),
                e.getStartLat(),
                e.getStartLon(),
                e.getEndLat(),
                e.getEndLon(),
                e.getWaypoints() != null ? e.getWaypoints() : new ArrayList<>(),
                e.getLengthM(),
                e.getMaxSpeedKmh(),
                e.getTrackCount(),
                Electrification.valueOf(e.getElectrification()),
                e.getGradePermil(),
                e.getStartStationId(),
                e.getEndStationId()
        );
    }

    public TrackSegmentEntity toEntity(TrackSegment t) {
        var entity = new TrackSegmentEntity();
        entity.setId(t.id());
        entity.setScenarioId(t.scenarioId());
        entity.setName(t.name());
        entity.setStartLat(t.startLat());
        entity.setStartLon(t.startLon());
        entity.setEndLat(t.endLat());
        entity.setEndLon(t.endLon());
        entity.setWaypoints(t.waypoints() != null ? t.waypoints() : new ArrayList<>());
        entity.setLengthM(t.lengthM());
        entity.setMaxSpeedKmh(t.maxSpeedKmh());
        entity.setTrackCount(t.trackCount());
        entity.setElectrification(t.electrification().name());
        entity.setGradePermil(t.gradePermil());
        entity.setStartStationId(t.startStationId());
        entity.setEndStationId(t.endStationId());
        return entity;
    }
}
