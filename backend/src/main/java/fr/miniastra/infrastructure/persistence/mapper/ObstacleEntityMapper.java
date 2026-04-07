package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.ObstacleType;
import fr.miniastra.infrastructure.persistence.entity.ObstacleEntity;
import org.springframework.stereotype.Component;

@Component
public class ObstacleEntityMapper {

    public Obstacle toDomain(ObstacleEntity e) {
        return new Obstacle(
                e.getId(),
                e.getScenarioId(),
                e.getName(),
                ObstacleType.valueOf(e.getType()),
                e.getTrackId(),
                e.getPositionM(),
                e.getLengthM(),
                e.isBlocking(),
                e.getSpeedLimitKmh(),
                e.getVisibilityM(),
                e.getAppearAtS(),
                e.getDisappearAtS()
        );
    }

    public ObstacleEntity toEntity(Obstacle o) {
        var entity = new ObstacleEntity();
        entity.setId(o.id());
        entity.setScenarioId(o.scenarioId());
        entity.setName(o.name());
        entity.setType(o.type().name());
        entity.setTrackId(o.trackId());
        entity.setPositionM(o.positionM());
        entity.setLengthM(o.lengthM());
        entity.setBlocking(o.blocking());
        entity.setSpeedLimitKmh(o.speedLimitKmh());
        entity.setVisibilityM(o.visibilityM());
        entity.setAppearAtS(o.appearAtS());
        entity.setDisappearAtS(o.disappearAtS());
        return entity;
    }
}
