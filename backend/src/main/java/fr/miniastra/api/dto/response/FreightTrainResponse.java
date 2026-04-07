package fr.miniastra.api.dto.response;

import fr.miniastra.domain.model.CargoType;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.TrainDirection;

import java.util.UUID;

public record FreightTrainResponse(
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
) {
    public static FreightTrainResponse from(FreightTrain t) {
        return new FreightTrainResponse(t.id(), t.scenarioId(), t.name(), t.modelCode(),
                t.trackId(), t.positionM(), t.direction(), t.initialSpeedKmh(),
                t.loadT(), t.cargoType());
    }
}
