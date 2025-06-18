package org.example.excelgeneration.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.example.excelgeneration.dto.DataObject.DTO_FIELDS;

/**
 * @author Aleksey
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class FastExcelGenerator {

    @Value("${files.upload-folder.fast-excel}")
    private Path fileLocation;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(fileLocation);
    }


    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());



    public String writeToFile(List<DataObject> objectList, String filename) {

        Path filePath = fileLocation.resolve(filename).normalize();

        var file = new File(filePath.toString());

        int row = 1;

        try (var fos = new FileOutputStream(file);
             var wb = new Workbook(fos, "Application", "1.0");
             Worksheet ws = wb.newWorksheet("Sheet 1")) {


            Field[] fields = DataObject.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
            }

            createHeaders(ws, DataObject.class);
            for (var object : objectList) {
                addObjectToRow(ws, row, object);
                row++;
            }
            wb.finish();
        } catch (IOException | IllegalAccessException e) {
            log.error("Error generating Excel file: {}", e.getMessage());
        }
        return filename;
    }

    private void createHeaders(Worksheet ws, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        int col = 0;
        for (var field : fields) {
            ws.value(0, col, field.getName());
            ++col;
        }
    }

    private void addObjectToRow(Worksheet ws, int row, DataObject object)
            throws IllegalAccessException {

        for (int col = 0; col < DTO_FIELDS.length; ++col) {
            Field field = DTO_FIELDS[col];
            Object value = field.get(object);

            switch (value) {
                case null -> ws.value(row, col, (String) null);
                case Number number -> ws.value(row, col, number);
                case Boolean b -> ws.value(row, col, b);
                case Instant instant -> ws.value(row, col, formatInstant(instant));
                case String s -> ws.value(row, col, s);
                default -> ws.value(row, col, value.toString());
            }
        }
    }

    private String formatInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DATETIME_FORMATTER);
    }

}
