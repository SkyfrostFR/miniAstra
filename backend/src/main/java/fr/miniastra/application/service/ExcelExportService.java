package fr.miniastra.application.service;

import fr.miniastra.application.export.FreightTrainSheetWriter;
import fr.miniastra.application.export.ObstacleSheetWriter;
import fr.miniastra.application.export.PassengerTrainSheetWriter;
import fr.miniastra.application.export.SignalSheetWriter;
import fr.miniastra.application.export.TrackSegmentSheetWriter;
import fr.miniastra.domain.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final ScenarioRepository scenarioRepository;
    private final TrackSegmentSheetWriter trackSegmentWriter;
    private final PassengerTrainSheetWriter passengerTrainWriter;
    private final FreightTrainSheetWriter freightTrainWriter;
    private final ObstacleSheetWriter obstacleWriter;
    private final SignalSheetWriter signalWriter;

    @Transactional(readOnly = true)
    public byte[] exportScenario(UUID scenarioId) {
        if (!scenarioRepository.existsById(scenarioId)) {
            throw new IllegalArgumentException("Scénario introuvable : id=" + scenarioId);
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            trackSegmentWriter.write(workbook, scenarioId);
            passengerTrainWriter.write(workbook, scenarioId);
            freightTrainWriter.write(workbook, scenarioId);
            obstacleWriter.write(workbook, scenarioId);
            signalWriter.write(workbook, scenarioId);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel", e);
        }
    }
}
