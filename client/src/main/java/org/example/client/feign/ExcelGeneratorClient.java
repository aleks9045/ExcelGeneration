package org.example.client.feign;

import org.example.client.config.FeignConfig;
import org.example.client.enums.GeneratorName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Aleksey
 */
@FeignClient(name = "excel-generator-api",
        url = "${generation.server.url}",
        configuration = FeignConfig.class)
public interface ExcelGeneratorClient {

    @GetMapping("/api/v1/generate")
    ResponseEntity<List<String>> generateExcelFiles(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") Long fileAmount,
            @RequestParam("object_amount") Long objectAmount,
            @RequestParam(value = "filename", required = false) String filename);

    @GetMapping("/api/v1/generateAsync")
    void generateExcelFilesAsync(
            @RequestParam("generator") GeneratorName generator,
            @RequestParam("file_amount") Long fileAmount,
            @RequestParam("object_amount") Long objectAmount,
            @RequestParam(value = "filename", required = false) String filename);
}
