package fr.miniastra.application.export;

import fr.miniastra.domain.model.FreightTrain;
import fr.miniastra.domain.repository.FreightTrainRepository;
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
public class FreightTrainSheetWriter implements SheetWriter {

    private final FreightTrainRepository repository;

    @Override
    public void write(SXSSFWorkbook workbook, UUID scenarioId) {
        Sheet sheet = workbook.createSheet("Trains fret");
        CellStyle headerStyle = TrackSegmentSheetWriter.createHeaderStyle(workbook);

        String[] headers = {"ID", "Nom", "Modèle", "Voie ID", "Position (m)",
                "Direction", "Vitesse initiale (km/h)", "Charge (t)", "Type fret"};
        TrackSegmentSheetWriter.writeHeader(sheet, headerStyle, headers);

        List<FreightTrain> trains = repository.findAllByScenarioId(scenarioId);
        int rowIdx = 1;
        for (FreightTrain t : trains) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.id().toString());
            row.createCell(1).setCellValue(t.name());
            row.createCell(2).setCellValue(t.modelCode());
            row.createCell(3).setCellValue(t.trackId().toString());
            row.createCell(4).setCellValue(t.positionM());
            row.createCell(5).setCellValue(t.direction().name());
            row.createCell(6).setCellValue(t.initialSpeedKmh());
            row.createCell(7).setCellValue(t.loadT());
            row.createCell(8).setCellValue(t.cargoType().name());
        }
    }
}
