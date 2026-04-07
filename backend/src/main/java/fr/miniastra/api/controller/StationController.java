package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateStationRequest;
import fr.miniastra.api.dto.response.StationResponse;
import fr.miniastra.api.exception.ConflictException;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService service;

    @GetMapping
    public List<StationResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(StationResponse::from).toList();
    }

    @GetMapping("/{id}")
    public StationResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var station = service.findById(id);
        if (!station.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Gare", id);
        return StationResponse.from(station);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StationResponse create(@PathVariable UUID scenarioId,
                                   @Valid @RequestBody CreateStationRequest req) {
        try {
            return StationResponse.from(service.create(scenarioId, req.name(), req.lat(), req.lon()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("existe déjà")) throw new ConflictException(e.getMessage());
            throw new ResourceNotFoundException("Scénario", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public StationResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                   @Valid @RequestBody CreateStationRequest req) {
        try {
            return StationResponse.from(service.update(scenarioId, id, req.name(), req.lat(), req.lon()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("existe déjà")) throw new ConflictException(e.getMessage());
            throw new ResourceNotFoundException("Gare", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Gare", id);
        }
    }
}
