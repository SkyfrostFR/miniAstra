package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.Scenario;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ScenarioResponse(
        UUID id,
        String name,
        String description,
        int durationS,
        LocalTime startTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ScenarioResponse from(Scenario s) {
        return new ScenarioResponse(s.id(), s.name(), s.description(), s.durationS(),
                s.startTime(), s.createdAt(), s.updatedAt());
    }
}
