package fr.miniastra.application.export;

import fr.miniastra.domain.model.Obstacle;
import fr.miniastra.domain.repository.ObstacleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObstacleSheetWriter implements SheetWriter {

    private final ObstacleRepository repository;

    @Override
    public void write(SXSSFWorkbook workbook, UUID scenarioId) {
        Sheet sheet = workbook.createSheet("Obstacles");
        CellStyle headerStyle = TrackSegmentSheetWriter.createHeaderStyle(workbook);

        String[] headers = {"ID", "Nom", "Type", "Voie ID", "Position (m)", "Longueur (m)",
                "Bloquant", "Limite vitesse (km/h)", "Visibilité (m)", "Apparaît (s)", "Disparaît (s)"};
        TrackSegmentSheetWriter.writeHeader(sheet, headerStyle, headers);

        List<Obstacle> obstacles = repository.findAllByScenarioId(scenarioId);
        int rowIdx = 1;
        for (Obstacle o : obstacles) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(o.id().toString());
            row.createCell(1).setCellValue(o.name());
            row.createCell(2).setCellValue(o.type().name());
            row.createCell(3).setCellValue(o.trackId().toString());
            row.createCell(4).setCellValue(o.positionM());
            row.createCell(5).setCellValue(o.lengthM());
            row.createCell(6).setCellValue(o.blocking());
            row.createCell(7).setCellValue(o.speedLimitKmh());
            row.createCell(8).setCellValue(o.visibilityM());
            row.createCell(9).setCellValue(o.appearAtS());
            row.createCell(10).setCellValue(o.disappearAtS() != null ? o.disappearAtS().toString() : "");
        }
    }
}
