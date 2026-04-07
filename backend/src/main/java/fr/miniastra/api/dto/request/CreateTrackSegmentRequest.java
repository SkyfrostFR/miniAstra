package fr.miniastra.api.dto.request;

import fr.miniastra.domain.model.Electrification;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record CreateTrackSegmentRequest(
        @NotBlank @Size(max = 100) String name,
        @DecimalMin("-90") @DecimalMax("90")   double startLat,
        @DecimalMin("-180") @DecimalMax("180") double startLon,
        @DecimalMin("-90") @DecimalMax("90")   double endLat,
        @DecimalMin("-180") @DecimalMax("180") double endLon,
        List<List<Double>> waypoints,
        @Min(0) int maxSpeedKmh,
        @Min(1) @Max(2) int trackCount,
        @NotNull Electrification electrification,
        @DecimalMin("-80") @DecimalMax("80") double gradePermil,
        UUID startStationId,
        UUID endStationId
) {
    public CreateTrackSegmentRequest {
        if (waypoints == null) waypoints = new ArrayList<>();
    }
}
