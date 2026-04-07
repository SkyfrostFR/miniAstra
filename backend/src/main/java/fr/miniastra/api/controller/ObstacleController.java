package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateObstacleRequest;
import fr.miniastra.api.dto.response.ObstacleResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.ObstacleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/obstacles")
@RequiredArgsConstructor
public class ObstacleController {

    private final ObstacleService service;

    @GetMapping
    public List<ObstacleResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(ObstacleResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ObstacleResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var obstacle = service.findById(id);
        if (!obstacle.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Obstacle", id);
        return ObstacleResponse.from(obstacle);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObstacleResponse create(@PathVariable UUID scenarioId,
                                   @Valid @RequestBody CreateObstacleRequest req) {
        try {
            return ObstacleResponse.from(service.create(scenarioId, req.name(), req.type(),
                    req.trackId(), req.positionM(), req.lengthM(), req.blocking(),
                    req.speedLimitKmh(), req.visibilityM(), req.appearAtS(), req.disappearAtS()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario ou tronçon", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public ObstacleResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                   @Valid @RequestBody CreateObstacleRequest req) {
        try {
            return ObstacleResponse.from(service.update(scenarioId, id, req.name(), req.type(),
                    req.trackId(), req.positionM(), req.lengthM(), req.blocking(),
                    req.speedLimitKmh(), req.visibilityM(), req.appearAtS(), req.disappearAtS()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Obstacle", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Obstacle", id);
        }
    }
}
