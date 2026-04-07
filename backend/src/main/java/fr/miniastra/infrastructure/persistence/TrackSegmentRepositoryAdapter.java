package fr.miniastra.infrastructure.persistence;

import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import fr.miniastra.infrastructure.persistence.mapper.TrackSegmentEntityMapper;
import fr.miniastra.infrastructure.persistence.repository.TrackSegmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TrackSegmentRepositoryAdapter implements TrackSegmentRepository {

    private final TrackSegmentJpaRepository jpa;
    private final TrackSegmentEntityMapper mapper;

    @Override
    public List<TrackSegment> findAllByScenarioId(UUID scenarioId) {
        return jpa.findAllByScenarioId(scenarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TrackSegment> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TrackSegment> findByScenarioIdAndName(UUID scenarioId, String name) {
        return jpa.findByScenarioIdAndName(scenarioId, name).map(mapper::toDomain);
    }

    @Override
    public TrackSegment save(TrackSegment track) {
        return mapper.toDomain(jpa.save(mapper.toEntity(track)));
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
