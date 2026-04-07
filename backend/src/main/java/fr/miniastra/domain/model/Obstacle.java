package fr.miniastra.domain.model;

import java.util.UUID;

public record Obstacle(
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
) {}
