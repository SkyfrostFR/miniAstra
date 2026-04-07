package fr.miniastra.domain.model;

import java.util.UUID;

public record Signal(
        UUID id,
        UUID scenarioId,
        String name,
        SignalType type,
        UUID trackId,
        double positionM,
        SignalDirection direction,
        SignalState initialState
) {}
