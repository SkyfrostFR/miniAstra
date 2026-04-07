package fr.miniastra.domain.model;

import java.util.UUID;

public record PassengerTrain(
        UUID id,
        UUID scenarioId,
        String name,
        String modelCode,
        UUID trackId,
        double positionM,
        TrainDirection direction,
        double initialSpeedKmh,
        int passengerCount,
        String serviceNumber
) {}
