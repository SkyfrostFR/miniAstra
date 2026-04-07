package fr.miniastra.application.export;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.UUID;

public interface SheetWriter {
    void write(SXSSFWorkbook workbook, UUID scenarioId);
}
