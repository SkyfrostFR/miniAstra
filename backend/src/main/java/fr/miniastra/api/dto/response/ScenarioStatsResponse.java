package fr.miniastra.api.dto.response;

import fr.miniastra.application.dto.ScenarioStats;

public record ScenarioStatsResponse(
        long trackSegments,
        long passengerTrains,
        long freightTrains,
        long obstacles,
        long signals
) {
    public static ScenarioStatsResponse from(ScenarioStats stats) {
        return new ScenarioStatsResponse(
                stats.trackSegments(),
                stats.passengerTrains(),
                stats.freightTrains(),
                stats.obstacles(),
                stats.signals()
        );
    }
}
