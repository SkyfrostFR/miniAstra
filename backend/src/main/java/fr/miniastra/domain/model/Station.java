package fr.miniastra.domain.model;

import java.util.UUID;

public record Station(
        UUID id,
        UUID scenarioId,
        String name,
        double lat,
        double lon
) {}
