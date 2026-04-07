package fr.miniastra.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record CreateScenarioRequest(
        @NotBlank @Size(max = 100) String name,
        String description,
        @Min(1) int durationS,
        @NotNull LocalTime startTime
) {}
