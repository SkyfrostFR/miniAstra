package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.PassengerTrain;
import fr.miniastra.domain.repository.PassengerTrainRepository;
import fr.miniastra.infrastructure.persistence.mapper.PassengerTrainEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.PassengerTrainJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PassengerTrainRepositoryAdapter implements PassengerTrainRepository {

    private final PassengerTrainJpaRepository jpa;
    private final PassengerTrainEntityMapper mapper;

    @Override
    public List<PassengerTrain> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<PassengerTrain> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public PassengerTrain save(PassengerTrain train) {
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
