package org.example.excelgeneration.generator;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.example.excelgeneration.dto.DataObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.example.excelgeneration.dto.DataObject.DTO_FIELDS;

/**
 * @author Aleksey
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class SXSSFGenerator {

    @Value("${files.upload-folder.apache-poi}")
    private Path fileLocation;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(fileLocation);
    }

    // Buffer
    private static final int WINDOW_SIZE = 100;

    public String writeToFile(List<DataObject> objectList, String filename) {

        Path filePath = fileLocation.resolve(filename).normalize();

        var file = new File(filePath.toString());

        try (var fos = new FileOutputStream(file);
             SXSSFWorkbook workbook = new SXSSFWorkbook(WINDOW_SIZE)) {

            workbook.setCompressTempFiles(true);

            SXSSFSheet sheet = workbook.createSheet("Sheet 1");

            // Создаем строку заголовков
            Row headerRow = sheet.createRow(0);
            createHeaders(headerRow, DataObject.class);

            // Заполняем данными
            int rowNum = 1;
            for (DataObject object : objectList) {
                addObjectRow(sheet.createRow(rowNum++), object);
            }

            workbook.write(fos);

        } catch (IOException | IllegalAccessException e) {
            log.error("Error generating Excel file: {}", e.getMessage());
        }
        return filename;
    }

    private void createHeaders(Row headerRow, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
        }
    }

    private void addObjectRow(Row row, DataObject object)
            throws IllegalAccessException {

        for (int col = 0; col < DTO_FIELDS.length; ++col) {
            Field field = DTO_FIELDS[col];
            Object value = field.get(object);

            Cell cell = row.createCell(col);
            setCellValue(cell, value);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        switch (value) {
            case null -> cell.setBlank();
            case Number num -> cell.setCellValue(num.doubleValue());

            // Форматирование для BigDecimal
//        if (value instanceof BigDecimal bd) {
//                CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
//                style.setDataFormat(NUMBER_FORMAT.getFormat("#,##0.00"));
//                cell.setCellStyle(style);
//        }
            case Boolean bool -> cell.setCellValue(bool);
            case Instant instant -> cell.setCellValue(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            case String str -> cell.setCellValue(str);
            default -> cell.setCellValue(value.toString());
        }
    }

}
