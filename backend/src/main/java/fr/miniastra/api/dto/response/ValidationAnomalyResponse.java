package fr.miniastra.api.dto.response;

import fr.miniastra.application.dto.ValidationAnomaly;

import java.util.UUID;

public record ValidationAnomalyResponse(
        String objectType,
        UUID objectId,
        String objectName,
        String rule,
        String description
) {
    public static ValidationAnomalyResponse from(ValidationAnomaly anomaly) {
        return new ValidationAnomalyResponse(
                anomaly.objectType(),
                anomaly.objectId(),
                anomaly.objectName(),
                anomaly.rule(),
                anomaly.description()
        );
    }
}
