package fr.miniastra.domain.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record Scenario(
        UUID id,
        String name,
        String description,
        int durationS,
        LocalTime startTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
