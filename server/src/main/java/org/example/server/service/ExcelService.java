package org.example.server.service;

import org.example.server.enums.GeneratorName;

import java.util.List;
import java.util.Optional;

/**
 * @author Aleksey
 */
public interface ExcelService {

    List<String> generateFiles(GeneratorName generator,
                               Long file_amount,
                               Long object_amount,
                               Optional<String> filename);

}
