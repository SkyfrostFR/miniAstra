package fr.miniastra.api.controller;

import fr.miniastra.api.dto.request.CreateTrackSegmentRequest;
import fr.miniastra.api.dto.response.TrackSegmentResponse;
import fr.miniastra.api.exception.ConflictException;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.TrackSegmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios/{scenarioId}/track-segments")
@RequiredArgsConstructor
public class TrackSegmentController {

    private final TrackSegmentService service;

    @GetMapping
    public List<TrackSegmentResponse> findAll(@PathVariable UUID scenarioId) {
        return service.findAllByScenarioId(scenarioId).stream()
                .map(TrackSegmentResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TrackSegmentResponse findById(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        var track = service.findById(id);
        if (!track.scenarioId().equals(scenarioId)) throw new ResourceNotFoundException("Tronçon", id);
        return TrackSegmentResponse.from(track);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackSegmentResponse create(@PathVariable UUID scenarioId,
                                       @Valid @RequestBody CreateTrackSegmentRequest req) {
        try {
            return TrackSegmentResponse.from(service.create(
                    scenarioId, req.name(), req.startLat(), req.startLon(),
                    req.endLat(), req.endLon(), req.waypoints(),
                    req.maxSpeedKmh(), req.trackCount(), req.electrification(), req.gradePermil(),
                    req.startStationId(), req.endStationId()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("existe déjà")) throw new ConflictException(e.getMessage());
            throw new ResourceNotFoundException("Scénario", scenarioId);
        }
    }

    @PutMapping("/{id}")
    public TrackSegmentResponse update(@PathVariable UUID scenarioId, @PathVariable UUID id,
                                       @Valid @RequestBody CreateTrackSegmentRequest req) {
        try {
            return TrackSegmentResponse.from(service.update(
                    scenarioId, id, req.name(), req.startLat(), req.startLon(),
                    req.endLat(), req.endLon(), req.waypoints(),
                    req.maxSpeedKmh(), req.trackCount(), req.electrification(), req.gradePermil(),
                    req.startStationId(), req.endStationId()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("existe déjà")) throw new ConflictException(e.getMessage());
            throw new ResourceNotFoundException("Tronçon", id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID scenarioId, @PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Tronçon", id);
        }
    }
}
