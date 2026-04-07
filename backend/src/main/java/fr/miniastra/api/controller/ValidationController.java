package fr.miniastra.api.controller;

import fr.miniastra.api.dto.response.ValidationReportResponse;
import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService validationService;

    @GetMapping("/{id}/validation-report")
    public ValidationReportResponse getValidationReport(@PathVariable UUID id) {
        try {
            var anomalies = validationService.validate(id);
            return ValidationReportResponse.from(id, anomalies);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }
    }
}
