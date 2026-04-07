package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.CargoType;
import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.infrastructure.persistence.entity.FreightTrainEntity;
import org.springframework.stereotype.Component;

@Component
public class FreightTrainEntityMapper {

    public FreightTrain toDomain(FreightTrainEntity e) {
        return new FreightTrain(
                e.getId(),
                e.getScenarioId(),
                e.getName(),
                e.getModelCode(),
                e.getTrackId(),
                e.getPositionM(),
                TrainDirection.valueOf(e.getDirection()),
                e.getInitialSpeedKmh(),
                e.getLoadT(),
                CargoType.valueOf(e.getCargoType())
        );
    }

    public FreightTrainEntity toEntity(FreightTrain t) {
        var entity = new FreightTrainEntity();
        entity.setId(t.id());
        entity.setScenarioId(t.scenarioId());
        entity.setName(t.name());
        entity.setModelCode(t.modelCode());
        entity.setTrackId(t.trackId());
        entity.setPositionM(t.positionM());
        entity.setDirection(t.direction().name());
        entity.setInitialSpeedKmh(t.initialSpeedKmh());
        entity.setLoadT(t.loadT());
        entity.setCargoType(t.cargoType().name());
        return entity;
    }
}
