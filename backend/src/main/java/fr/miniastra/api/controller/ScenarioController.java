package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateScenarioRequest;
import fr.miniastra.api.dto.response.ScenarioResponse;
import fr.miniastra.api.dto.response.ScenarioStatsResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.ScenarioService;
import fr.miniastra.application.service.ScenarioStatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService service;
    private final ScenarioStatsService statsService;

    @GetMapping
    public List<ScenarioResponse> findAll() {
        return service.findAll().stream().map(ScenarioResponse::from).toList();
    }

    @GetMapping("/last-used")
    public ScenarioResponse findLastUsed() {
        return service.findMostRecentlyUpdated()
                .map(ScenarioResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Scénario", null));
    }

    @GetMapping("/{id}/stats")
    public ScenarioStatsResponse getStats(@PathVariable UUID id) {
        try {
            return ScenarioStatsResponse.from(statsService.getStats(id));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }

    @GetMapping("/{id}")
    public ScenarioResponse findById(@PathVariable UUID id) {
        try {
            return ScenarioResponse.from(service.findById(id));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScenarioResponse create(@Valid @RequestBody CreateScenarioRequest req) {
        return ScenarioResponse.from(
                service.create(req.name(), req.description(), req.durationS(), req.startTime()));
    }

    @PutMapping("/{id}")
    public ScenarioResponse update(@PathVariable UUID id, @Valid @RequestBody CreateScenarioRequest req) {
        try {
            return ScenarioResponse.from(
                    service.update(id, req.name(), req.description(), req.durationS(), req.startTime()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }
}
