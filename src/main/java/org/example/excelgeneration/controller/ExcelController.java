package org.example.excelgeneration.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.excelgeneration.enums.GeneratorName;
import org.example.excelgeneration.service.ExcelService;
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
            @RequestParam("file_amount") @Positive @Min(1) Long file_amount,
            @RequestParam("object_amount") @Positive @Min(1) Long object_amount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {

        var generatedFileNames = excelService.generateFiles(generator,
                file_amount,
                object_amount,
                filename);

        return ResponseEntity.ok(generatedFileNames);
    }
}
