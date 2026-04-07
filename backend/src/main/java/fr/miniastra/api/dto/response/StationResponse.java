package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.Station;

import java.util.UUID;

public record StationResponse(
        UUID id,
        UUID scenarioId,
        String name,
        double lat,
        double lon
) {
    public static StationResponse from(Station s) {
        return new StationResponse(s.id(), s.scenarioId(), s.name(), s.lat(), s.lon());
    }
}
