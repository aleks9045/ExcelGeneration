package org.example.server.service;

import org.example.server.enums.GeneratorName;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Aleksey
 */
public interface ExcelService {

    @Async
    void generateFilesAsync(GeneratorName generator,
                                    Long fileAmount,
                                    Long objectAmount,
                                    Optional<String> filename);

    List<String> generateFiles(GeneratorName generator,
                               Long file_amount,
                               Long object_amount,
                               Optional<String> filename);

}
