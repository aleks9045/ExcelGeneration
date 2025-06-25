package org.example.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.client.enums.GeneratorName;
import org.example.client.feign.ActuatorClient;
import org.example.client.feign.ExcelGeneratorClient;
import org.example.client.service.ExcelGenerationClientService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Aleksey
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ExcelGenerationClientServiceImpl implements ExcelGenerationClientService {

    private final ExcelGeneratorClient excelGeneratorClient;
    private final ActuatorClient actuatorClient;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final ObjectMapper objectMapper;

    public List<String> generateFiles(GeneratorName generator,
                                      Long fileAmount,
                                      Long objectAmount,
                                      Optional<String> filename) {


        ScheduledFuture<?> monitoringTask = scheduler.scheduleAtFixedRate(
                this::logServerMetrics,
                1, 1, TimeUnit.SECONDS);

        try {
            var startTime = Instant.now();
            log.info("Sent new request | GENERATOR: {}, FILE AMOUNT: {}, OBJECT AMOUNT: {}",
                    generator, fileAmount, objectAmount);

            var response = excelGeneratorClient.generateExcelFiles(
                    generator,
                    fileAmount,
                    objectAmount,
                    filename.orElse(null));

            var endTime = Instant.now();

            var seconds = Duration.between(startTime, endTime).toMillis() / 1000.0;

            log.info("Request completed");
            log.info("Time spent: {}s", seconds);
            return response.getBody();

        } finally {
            monitoringTask.cancel(true);
        }
    }

    private void logServerMetrics() {
        try {

            var cpuMetricMap = actuatorClient.getMetric("system.cpu.usage").getBody();

            var cpuUsage = Objects.requireNonNull(this.getValueFromMetricMap(cpuMetricMap))
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            var memoryMetricMap = actuatorClient.getMetric("jvm.memory.used").getBody();

            var memoryUsage = Objects.requireNonNull(this.getValueFromMetricMap(memoryMetricMap))
                    .divide(BigDecimal.valueOf(1024 * 1024), RoundingMode.HALF_UP)
                    .setScale(0, RoundingMode.HALF_UP);

            log.info("Request load | CPU: {}%, Memory: {} MB", cpuUsage, memoryUsage);

        } catch (Exception e) {
            log.error("Failed to fetch metrics: {}", e.getMessage());
        }
    }

    private BigDecimal getValueFromMetricMap(Map<String, Object> metricMap) {
        var jsonMetrics = objectMapper.valueToTree(metricMap);
        if (jsonMetrics.has("measurements")) {
            var firstMeasurement = jsonMetrics.get("measurements").get(0);
            return firstMeasurement.get("value").decimalValue();
        }
        return null;
    }
}
