package fr.miniastra.api.controller;

import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.GeoJsonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class GeoJsonController {

    private final GeoJsonService geoJsonService;

    @GetMapping("/{id}/geojson")
    public Map<String, Object> getGeoJson(@PathVariable UUID id) {
        try {
            return geoJsonService.buildFeatureCollection(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }
}
