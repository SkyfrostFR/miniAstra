package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.Station;
import fr.miniastra.infrastructure.persistence.entity.StationEntity;
import org.springframework.stereotype.Component;

@Component
public class StationEntityMapper {

    public Station toDomain(StationEntity e) {
        return new Station(e.getId(), e.getScenarioId(), e.getName(), e.getLat(), e.getLon());
    }

    public StationEntity toEntity(Station s) {
        var entity = new StationEntity();
        entity.setId(s.id());
        entity.setScenarioId(s.scenarioId());
        entity.setName(s.name());
        entity.setLat(s.lat());
        entity.setLon(s.lon());
        return entity;
    }
}
