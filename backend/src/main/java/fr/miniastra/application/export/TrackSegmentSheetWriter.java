package fr.miniastra.application.export;

import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.repository.TrackSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrackSegmentSheetWriter implements SheetWriter {

    private final TrackSegmentRepository repository;

    @Override
    public void write(SXSSFWorkbook workbook, UUID scenarioId) {
        Sheet sheet = workbook.createSheet("Voies");
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"ID", "Nom", "Début Lat", "Début Lon", "Fin Lat", "Fin Lon",
                "Longueur (m)", "Vitesse max (km/h)", "Voies", "Électrification", "Pente (‰)"};
        writeHeader(sheet, headerStyle, headers);

        List<TrackSegment> tracks = repository.findAllByScenarioId(scenarioId);
        int rowIdx = 1;
        for (TrackSegment t : tracks) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.id().toString());
            row.createCell(1).setCellValue(t.name());
            row.createCell(2).setCellValue(t.startLat());
            row.createCell(3).setCellValue(t.startLon());
            row.createCell(4).setCellValue(t.endLat());
            row.createCell(5).setCellValue(t.endLon());
            row.createCell(6).setCellValue(t.lengthM());
            row.createCell(7).setCellValue(t.maxSpeedKmh());
            row.createCell(8).setCellValue(t.trackCount());
            row.createCell(9).setCellValue(t.electrification().name());
            row.createCell(10).setCellValue(t.gradePermil());
        }
    }

    static void writeHeader(Sheet sheet, CellStyle style, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    static CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
