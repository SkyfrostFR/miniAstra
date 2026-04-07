package fr.miniastra.api.dto.response;

import fr.miniastra.application.dto.ValidationAnomaly;

import java.util.List;
import java.util.UUID;

public record ValidationReportResponse(
        UUID scenarioId,
        int anomalyCount,
        List<ValidationAnomalyResponse> anomalies
) {
    public static ValidationReportResponse from(UUID scenarioId, List<ValidationAnomaly> anomalies) {
        List<ValidationAnomalyResponse> responses = anomalies.stream()
                .map(ValidationAnomalyResponse::from)
                .toList();
        return new ValidationReportResponse(scenarioId, responses.size(), responses);
    }
}
