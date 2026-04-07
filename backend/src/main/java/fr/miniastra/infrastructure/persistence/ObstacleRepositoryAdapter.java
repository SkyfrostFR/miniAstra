package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.repository.ObstacleRepository;
import fr.miniastra.infrastructure.persistence.mapper.ObstacleEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.ObstacleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ObstacleRepositoryAdapter implements ObstacleRepository {

    private final ObstacleJpaRepository jpa;
    private final ObstacleEntityMapper mapper;

    @Override
    public List<Obstacle> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Obstacle> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Obstacle save(Obstacle obstacle) {
        return mapper.toDomain(jpa.save(mapper.toEntity(obstacle)));
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
