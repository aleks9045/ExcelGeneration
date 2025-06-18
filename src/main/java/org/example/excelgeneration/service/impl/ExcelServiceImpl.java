package org.example.excelgeneration.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.excelgeneration.dto.DataObject;
import org.example.excelgeneration.enums.GeneratorName;
import org.example.excelgeneration.generator.FastExcelGenerator;
import org.example.excelgeneration.generator.SXSSFGenerator;
import org.example.excelgeneration.service.ExcelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @author Aleksey
 */
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final FastExcelGenerator fastExcelGenerator;
    private final SXSSFGenerator sxssfGenerator;

    @Override
    public List<String> generateFiles(GeneratorName generator,
                                      Long file_amount,
                                      Long object_amount,
                                      Optional<String> filename) {

        var fileNameList = new ArrayList<String>();
        BiFunction<Long, String, String> function = null;

        if (generator.equals(GeneratorName.FAST_EXCEL)) {
            function = this::generateByFastExcel;
        }
        if (generator.equals(GeneratorName.APACHE_POI)) {
            function = this::generateByApachePOI;
        }

        for (long i = 0; i < file_amount; ++i) {

            String generatedFileName = function.apply(
                    object_amount,
                    filename.orElse(UUID.randomUUID().toString()) + i + 1);

            fileNameList.add(generatedFileName);
        }
        return fileNameList;
    }


    private String generateByFastExcel(Long object_amount, String filename) {
        List<DataObject> dataObjects = DataObject.getRandomInstanceList(object_amount);
        return fastExcelGenerator.writeToFile(dataObjects, filename);
    }

    private String generateByApachePOI(Long object_amount, String filename) {
        List<DataObject> dataObjects = DataObject.getRandomInstanceList(object_amount);
        return sxssfGenerator.writeToFile(dataObjects, filename);
    }
}
