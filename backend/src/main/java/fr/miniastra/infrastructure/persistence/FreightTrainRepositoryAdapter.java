package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.repository.FreightTrainRepository;
import fr.miniastra.infrastructure.persistence.mapper.FreightTrainEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.FreightTrainJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FreightTrainRepositoryAdapter implements FreightTrainRepository {

    private final FreightTrainJpaRepository jpa;
    private final FreightTrainEntityMapper mapper;

    @Override
    public List<FreightTrain> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<FreightTrain> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public FreightTrain save(FreightTrain train) {
        return mapper.toDomain(jpa.save(mapper.toEntity(train)));
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
