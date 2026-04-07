package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreatePassengerTrainRequest;
import fr.miniastra.api.dto.response.PassengerTrainResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.PassengerTrainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/passenger-trains")
@RequiredArgsConstructor
public class PassengerTrainController {

    private final PassengerTrainService service;

    @GetMapping
    public List<PassengerTrainResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(PassengerTrainResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PassengerTrainResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var train = service.findById(id);
        if (!train.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Train passager", id);
        return PassengerTrainResponse.from(train);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerTrainResponse create(@PathVariable UUID scenarioId,
                                         @Valid @RequestBody CreatePassengerTrainRequest req) {
        try {
            return PassengerTrainResponse.from(service.create(scenarioId, req.name(), req.modelCode(),
                    req.trackId(), req.positionM(), req.direction(), req.initialSpeedKmh(),
                    req.passengerCount(), req.serviceNumber()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario ou tronçon", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public PassengerTrainResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                         @Valid @RequestBody CreatePassengerTrainRequest req) {
        try {
            return PassengerTrainResponse.from(service.update(scenarioId, id, req.name(), req.modelCode(),
                    req.trackId(), req.positionM(), req.direction(), req.initialSpeedKmh(),
                    req.passengerCount(), req.serviceNumber()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Train passager", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Train passager", id);
        }
    }
}
