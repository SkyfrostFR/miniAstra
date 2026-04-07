package fr.miniastra.api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStationRequest(
        @NotBlank @Size(max = 100) String name,
        @DecimalMin("-90")  @DecimalMax("90")  double lat,
        @DecimalMin("-180") @DecimalMax("180") double lon
) {}
