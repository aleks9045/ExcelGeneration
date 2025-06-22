package org.example.client.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.client.enums.GeneratorName;
import org.example.client.service.ExcelGenerationClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam("fileAmount") @Min(1) Long fileAmount,
            @RequestParam("objectAmount") @Min(1) Long objectAmount,
            @RequestParam(value = "filename", required = false) Optional<String> filename) {
        List<String> generatedFiles = excelGenerationClientService.generateFiles(
                generator,
                fileAmount,
                objectAmount,
                filename
        );

        return ResponseEntity.ok(generatedFiles);
    }
}
