package fr.miniastra.domain.model;

import java.util.UUID;

public record FreightTrain(
        UUID id,
        UUID scenarioId,
        String name,
        String modelCode,
        UUID trackId,
        double positionM,
        TrainDirection direction,
        double initialSpeedKmh,
        double loadT,
        CargoType cargoType
) {}
