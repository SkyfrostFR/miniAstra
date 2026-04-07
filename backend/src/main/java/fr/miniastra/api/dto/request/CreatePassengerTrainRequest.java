package fr.miniastra.api.dto.request;

import fr.miniastra.domain.model.TrainDirection;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreatePassengerTrainRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 30)  String modelCode,
        @NotNull UUID trackId,
        @Min(0) double positionM,
        @NotNull TrainDirection direction,
        @Min(0) double initialSpeedKmh,
        @Min(0) int passengerCount,
        @Size(max = 20) String serviceNumber
) {}
