package fr.miniastra.api.dto.request;

import fr.miniastra.domain.model.ObstacleType;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateObstacleRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull ObstacleType type,
        @NotNull UUID trackId,
        @Min(0) double positionM,
        @Min(0) double lengthM,
        boolean blocking,
        @Min(0) int speedLimitKmh,
        @Min(0) double visibilityM,
        @Min(0) int appearAtS,
        Integer disappearAtS
) {}
