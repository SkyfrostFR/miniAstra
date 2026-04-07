package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateFreightTrainRequest;
import fr.miniastra.api.dto.response.FreightTrainResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.FreightTrainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/freight-trains")
@RequiredArgsConstructor
public class FreightTrainController {

    private final FreightTrainService service;

    @GetMapping
    public List<FreightTrainResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(FreightTrainResponse::from).toList();
    }

    @GetMapping("/{id}")
    public FreightTrainResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var train = service.findById(id);
        if (!train.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Train fret", id);
        return FreightTrainResponse.from(train);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FreightTrainResponse create(@PathVariable UUID scenarioId,
                                       @Valid @RequestBody CreateFreightTrainRequest req) {
        try {
            return FreightTrainResponse.from(service.create(scenarioId, req.name(), req.modelCode(),
                    req.trackId(), req.positionM(), req.direction(), req.initialSpeedKmh(),
                    req.loadT(), req.cargoType()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario ou tronçon", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public FreightTrainResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                       @Valid @RequestBody CreateFreightTrainRequest req) {
        try {
            return FreightTrainResponse.from(service.update(scenarioId, id, req.name(), req.modelCode(),
                    req.trackId(), req.positionM(), req.direction(), req.initialSpeedKmh(),
                    req.loadT(), req.cargoType()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Train fret", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Train fret", id);
        }
    }
}
