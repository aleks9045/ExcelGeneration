package org.example.server.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.server.enums.GeneratorName;
import org.example.server.service.ExcelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Aleksey
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/generate")
    public ResponseEntity<List<String>> generateExcelFiles(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") @Positive @Min(1) Long fileAmount,
            @RequestParam("object_amount") @Positive @Min(1) Long objectAmount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {

        var generatedFileNames = excelService.generateFiles(generator,
                fileAmount,
                objectAmount,
                filename);

        return ResponseEntity.ok(generatedFileNames);
    }

    @GetMapping("/generateAsync")
    @ResponseStatus(HttpStatus.OK)
    public void generateExcelFilesAsync(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") @Positive @Min(1) Long fileAmount,
            @RequestParam("object_amount") @Positive @Min(1) Long objectAmount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {

        excelService.generateFilesAsync(generator,
                fileAmount,
                objectAmount,
                filename);
    }
}
