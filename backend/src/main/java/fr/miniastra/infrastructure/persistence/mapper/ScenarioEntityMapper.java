package fr.miniastra.infrastructure.persistence.mapper;

import fr.miniastra.domain.model.Scenario;
import fr.miniastra.infrastructure.persistence.entity.ScenarioEntity;
import org.springframework.stereotype.Component;

@Component
public class ScenarioEntityMapper {

    public Scenario toDomain(ScenarioEntity e) {
        return new Scenario(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getDurationS(),
                e.getStartTime(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public ScenarioEntity toEntity(Scenario s) {
        var entity = new ScenarioEntity();
        entity.setId(s.id());
        entity.setName(s.name());
        entity.setDescription(s.description());
        entity.setDurationS(s.durationS());
        entity.setStartTime(s.startTime());
        if (s.createdAt() != null) entity.setCreatedAt(s.createdAt());
        return entity;
    }
}
