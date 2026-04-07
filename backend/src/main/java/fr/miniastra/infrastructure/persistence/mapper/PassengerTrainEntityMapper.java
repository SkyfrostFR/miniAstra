package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.infrastructure.persistence.entity.PassengerTrainEntity;
import org.springframework.stereotype.Component;

@Component
public class PassengerTrainEntityMapper {

    public PassengerTrain toDomain(PassengerTrainEntity e) {
        return new PassengerTrain(
                e.getId(),
                e.getScenarioId(),
                e.getName(),
                e.getModelCode(),
                e.getTrackId(),
                e.getPositionM(),
                TrainDirection.valueOf(e.getDirection()),
                e.getInitialSpeedKmh(),
                e.getPassengerCount(),
                e.getServiceNumber()
        );
    }

    public PassengerTrainEntity toEntity(PassengerTrain t) {
        var entity = new PassengerTrainEntity();
        entity.setId(t.id());
        entity.setScenarioId(t.scenarioId());
        entity.setName(t.name());
        entity.setModelCode(t.modelCode());
        entity.setTrackId(t.trackId());
        entity.setPositionM(t.positionM());
        entity.setDirection(t.direction().name());
        entity.setInitialSpeedKmh(t.initialSpeedKmh());
        entity.setPassengerCount(t.passengerCount());
        entity.setServiceNumber(t.serviceNumber());
        return entity;
    }
}
