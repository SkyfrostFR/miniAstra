package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.Station;
import fr.miniastra.domain.repository.StationRepository;
import fr.miniastra.infrastructure.persistence.mapper.StationEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.StationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StationRepositoryAdapter implements StationRepository {

    private final StationJpaRepository jpa;
    private final StationEntityMapper mapper;

    @Override
    public List<Station> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Station> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Station> findByScenarioIdAndName(UUID scenarioId, String name) {
        return jpa.findByScenarioIdAndName(scenarioId, name).map(mapper::toDomain);
    }

    @Override
    public Station save(Station station) {
        return mapper.toDomain(jpa.save(mapper.toEntity(station)));
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }
}
