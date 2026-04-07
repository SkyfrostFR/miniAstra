package fr.miniastra.api.controller;

import fr.miniastra.api.exception.ResourceNotFoundException;
import fr.miniastra.application.service.ExcelExportService;
import fr.miniastra.application.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ExcelExportController {

    private final ExcelExportService exportService;
    private final ScenarioService scenarioService;

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable UUID id) {
        String scenarioName;
        try {
            scenarioName = scenarioService.findById(id).name();
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }

        byte[] xlsx;
        try {
            xlsx = exportService.exportScenario(id);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Scénario", id);
        }

        String filename = "scenario-" + scenarioName.replaceAll("[^a-zA-Z0-9_-]", "_")
                + "-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(xlsx);
    }
}
