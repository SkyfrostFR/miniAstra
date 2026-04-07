package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.model.SignalDirection;
import fr.miniastra.domain.model.SignalState;
import fr.miniastra.domain.model.SignalType;
import fr.miniastra.infrastructure.persistence.entity.SignalEntity;
import org.springframework.stereotype.Component;

@Component
public class SignalEntityMapper {

    public Signal toDomain(SignalEntity e) {
        return new Signal(
                e.getId(),
                e.getScenarioId(),
                e.getName(),
                SignalType.valueOf(e.getType()),
                e.getTrackId(),
                e.getPositionM(),
                SignalDirection.valueOf(e.getDirection()),
                SignalState.valueOf(e.getInitialState())
        );
    }

    public SignalEntity toEntity(Signal s) {
        var entity = new SignalEntity();
        entity.setId(s.id());
        entity.setScenarioId(s.scenarioId());
        entity.setName(s.name());
        entity.setType(s.type().name());
        entity.setTrackId(s.trackId());
        entity.setPositionM(s.positionM());
        entity.setDirection(s.direction().name());
        entity.setInitialState(s.initialState().name());
        return entity;
    }
}
