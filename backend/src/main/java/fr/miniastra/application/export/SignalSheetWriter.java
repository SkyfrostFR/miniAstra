package fr.miniastra.application.export;

import fr.miniastra.domain.model.Signal;
import fr.miniastra.domain.repository.SignalRepository;
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
public class SignalSheetWriter implements SheetWriter {

    private final SignalRepository repository;

    @Override
    public void write(SXSSFWorkbook workbook, UUID scenarioId) {
        Sheet sheet = workbook.createSheet("Signaux");
        CellStyle headerStyle = TrackSegmentSheetWriter.createHeaderStyle(workbook);

        String[] headers = {"ID", "Nom", "Type", "Voie ID", "Position (m)", "Direction", "État initial"};
        TrackSegmentSheetWriter.writeHeader(sheet, headerStyle, headers);

        List<Signal> signals = repository.findAllByScenarioId(scenarioId);
        int rowIdx = 1;
        for (Signal s : signals) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.id().toString());
            row.createCell(1).setCellValue(s.name());
            row.createCell(2).setCellValue(s.type().name());
            row.createCell(3).setCellValue(s.trackId().toString());
            row.createCell(4).setCellValue(s.positionM());
            row.createCell(5).setCellValue(s.direction().name());
            row.createCell(6).setCellValue(s.initialState().name());
        }
    }
}
