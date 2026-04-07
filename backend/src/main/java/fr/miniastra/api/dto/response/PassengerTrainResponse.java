package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.TrainDirection;

import java.util.UUID;

public record PassengerTrainResponse(
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
) {
    public static PassengerTrainResponse from(PassengerTrain t) {
        return new PassengerTrainResponse(t.id(), t.scenarioId(), t.name(), t.modelCode(),
                t.trackId(), t.positionM(), t.direction(), t.initialSpeedKmh(),
                t.passengerCount(), t.serviceNumber());
    }
}
