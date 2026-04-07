package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.model.ObstacleType;

import java.util.UUID;

public record ObstacleResponse(
        UUID id,
        UUID scenarioId,
        String name,
        ObstacleType type,
        UUID trackId,
        double positionM,
        double lengthM,
        boolean blocking,
        int speedLimitKmh,
        double visibilityM,
        int appearAtS,
        Integer disappearAtS
) {
    public static ObstacleResponse from(Obstacle o) {
        return new ObstacleResponse(o.id(), o.scenarioId(), o.name(), o.type(), o.trackId(),
                o.positionM(), o.lengthM(), o.blocking(), o.speedLimitKmh(),
                o.visibilityM(), o.appearAtS(), o.disappearAtS());
    }
}
