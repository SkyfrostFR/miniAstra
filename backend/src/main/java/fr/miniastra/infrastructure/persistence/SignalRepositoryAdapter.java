package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.repository.SignalRepository;
import fr.miniastra.infrastructure.persistence.mapper.SignalEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.SignalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SignalRepositoryAdapter implements SignalRepository {

    private final SignalJpaRepository jpa;
    private final SignalEntityMapper mapper;

    @Override
    public List<Signal> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Signal> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Signal save(Signal signal) {
        return mapper.toDomain(jpa.save(mapper.toEntity(signal)));
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
