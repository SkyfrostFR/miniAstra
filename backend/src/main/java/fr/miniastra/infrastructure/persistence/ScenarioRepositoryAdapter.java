package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.Scenario;
import fr.miniastra.domain.repository.ScenarioRepository;
import fr.miniastra.infrastructure.persistence.mapper.ScenarioEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.ScenarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ScenarioRepositoryAdapter implements ScenarioRepository {

    private final ScenarioJpaRepository jpa;
    private final ScenarioEntityMapper mapper;

    @Override
    public List<Scenario> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Scenario> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Scenario> findMostRecentlyUpdated() {
        return jpa.findFirstByOrderByUpdatedAtDesc().map(mapper::toDomain);
    }

    @Override
    public Scenario save(Scenario scenario) {
        var entity = mapper.toEntity(scenario);
        return mapper.toDomain(jpa.save(entity));
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
