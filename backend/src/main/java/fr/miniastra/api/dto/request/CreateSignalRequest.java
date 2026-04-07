package fr.miniastra.api.dto.request;

import fr.miniastra.domain.model.SignalDirection;
import fr.miniastra.domain.model.SignalState;
import fr.miniastra.domain.model.SignalType;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateSignalRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull SignalType type,
        @NotNull UUID trackId,
        @Min(0) double positionM,
        @NotNull SignalDirection direction,
        @NotNull SignalState initialState
) {}
