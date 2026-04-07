package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.SignalDirection;
import fr.miniastra.domain.model.SignalState;
import fr.miniastra.domain.model.SignalType;

import java.util.UUID;

public record SignalResponse(
        UUID id,
        UUID scenarioId,
        String name,
        SignalType type,
        UUID trackId,
        double positionM,
        SignalDirection direction,
        SignalState initialState
) {
    public static SignalResponse from(Signal s) {
        return new SignalResponse(s.id(), s.scenarioId(), s.name(), s.type(), s.trackId(),
                s.positionM(), s.direction(), s.initialState());
    }
}
