package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateSignalRequest;
import fr.miniastra.api.dto.response.SignalResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.SignalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/signals")
@RequiredArgsConstructor
public class SignalController {

    private final SignalService service;

    @GetMapping
    public List<SignalResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(SignalResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SignalResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var signal = service.findById(id);
        if (!signal.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Signal", id);
        return SignalResponse.from(signal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SignalResponse create(@PathVariable UUID scenarioId,
                                 @Valid @RequestBody CreateSignalRequest req) {
        try {
            return SignalResponse.from(service.create(scenarioId, req.name(), req.type(),
                    req.trackId(), req.positionM(), req.direction(), req.initialState()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario ou tronçon", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public SignalResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                 @Valid @RequestBody CreateSignalRequest req) {
        try {
            return SignalResponse.from(service.update(scenarioId, id, req.name(), req.type(),
                    req.trackId(), req.positionM(), req.direction(), req.initialState()));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Signal", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Signal", id);
        }
    }
}
