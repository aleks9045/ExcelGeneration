package org.example.server.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.server.dto.DataObject;
import org.example.server.enums.GeneratorName;
import org.example.server.generator.FastExcelGenerator;
import org.example.server.generator.SXSSFGenerator;
import org.example.server.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

/**
 * @author Aleksey
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ExcelServiceImpl implements ExcelService {

    private final ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> monitoringTask;
    private final AtomicInteger activeTasks = new AtomicInteger();
    @Autowired
    private MeterRegistry meterRegistry;
    private final FastExcelGenerator fastExcelGenerator;
    private final SXSSFGenerator sxssfGenerator;


    @Override
    @Async("excelTaskExecutor")
    public void generateFilesAsync(GeneratorName generator,
                                   Long fileAmount,
                                   Long objectAmount,
                                   Optional<String> filename) {

        var startTime = new AtomicLong(System.currentTimeMillis());
        this.startMonitoring();

        try {

            this.generateFiles(generator,
                    fileAmount,
                    objectAmount,
                    filename);

            long duration = (System.currentTimeMillis() - startTime.get()) / 1000;
            log.info("Time spent: {}s", duration);
        } catch (Exception e) {
            log.error("Error while generating files", e);
        } finally {
            this.stopMonitoring();
        }
    }

    public void startMonitoring() {
        if (activeTasks.getAndIncrement() == 0) {
            monitoringTask = monitorExecutor.scheduleAtFixedRate(
                    this::logSystemMetrics,
                    1, 1, TimeUnit.SECONDS
            );
        }
    }

    private void stopMonitoring() {
        if (activeTasks.decrementAndGet() == 0){
            monitoringTask.cancel(true);
        }
    }

    @Override
    public List<String> generateFiles(GeneratorName generator,
                                      Long fileAmount,
                                      Long objectAmount,
                                      Optional<String> filename) {
        log.info("Received request | GENERATOR: {}, FILE AMOUNT: {}, OBJECT AMOUNT: {}",
                generator, fileAmount, objectAmount);

        var fileNameList = new ArrayList<String>();
        BiFunction<Long, String, String> function = null;

        if (generator.equals(GeneratorName.FAST_EXCEL)) {
            function = this::generateByFastExcel;
        }
        if (generator.equals(GeneratorName.APACHE_POI)) {
            function = this::generateByApachePOI;
        }

        for (long i = 0; i < fileAmount; ++i) {

            String generatedFileName = function.apply(
                    objectAmount,
                    filename.orElse(UUID.randomUUID().toString()) + i + 1);

            fileNameList.add(generatedFileName);
        }
        log.info("Request performed");
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

    private void logSystemMetrics() {
        try {
            double cpuUsageValue = meterRegistry.get("system.cpu.usage")
                    .gauge().value();

            double memoryUsedValue = meterRegistry.get("jvm.memory.used")
                    .tag("area", "heap")
                    .gauge().value();

            BigDecimal cpuPercent = BigDecimal.valueOf(cpuUsageValue)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal memoryMB = BigDecimal.valueOf(memoryUsedValue)
                    .divide(BigDecimal.valueOf(1024 * 1024), 0, RoundingMode.HALF_UP);

            log.info("System Load | Active tasks: {}, CPU: {}%, Memory: {} MB",
                    activeTasks.get(),
                    cpuPercent,
                    memoryMB);
        } catch (Exception e) {
            log.warn("Failed to collect metrics", e);
        }
    }

    @PreDestroy
    public void monitorExecutorShutdown() {
        monitorExecutor.shutdown();
    }
}
