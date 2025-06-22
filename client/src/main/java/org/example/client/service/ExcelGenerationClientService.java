package org.example.client.service;

import org.example.client.enums.GeneratorName;

import java.util.List;
import java.util.Optional;

/**
 * @author Aleksey
 */

public interface ExcelGenerationClientService {

    List<String> generateFiles(GeneratorName generator,
                               Long fileAmount,
                               Long objectAmount,
                               Optional<String> filename);
}
