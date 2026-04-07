package fr.miniastra.application.dto;

public record ScenarioStats(
        long trackSegments,
        long passengerTrains,
        long freightTrains,
        long obstacles,
        long signals
) {}
