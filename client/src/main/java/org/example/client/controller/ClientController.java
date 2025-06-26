package org.example.client.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.client.enums.GeneratorName;
import org.example.client.service.ExcelGenerationClientService;
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
public class ClientController {

    private final ExcelGenerationClientService excelGenerationClientService;

    @GetMapping("/generate")
    public ResponseEntity<List<String>> generateExcelFiles(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") @Min(1) Long fileAmount,
            @RequestParam("object_amount") @Min(1) Long objectAmount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {
        var generatedFiles = excelGenerationClientService.generateFiles(
                generator,
                fileAmount,
                objectAmount,
                filename
        );

        return ResponseEntity.ok(generatedFiles);
    }

    @GetMapping("/generateAsync")
    @ResponseStatus(HttpStatus.OK)
    public void generateExcelFilesAsync(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") @Positive @Min(1) Long fileAmount,
            @RequestParam("object_amount") @Positive @Min(1) Long objectAmount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {

        excelGenerationClientService.generateFilesAsync(generator,
                fileAmount,
                objectAmount,
                filename);
    }
}
