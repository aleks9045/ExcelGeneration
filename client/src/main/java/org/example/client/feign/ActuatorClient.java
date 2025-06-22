package org.example.client.feign;

import org.example.client.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Aleksey
 */
@FeignClient(name = "actuator-client",
        url = "${generation.server.url}",
        configuration = FeignConfig.class)
public interface ActuatorClient {

    @GetMapping("/actuator/health")
    ResponseEntity<String> getHealth();

    @GetMapping("/actuator/metrics/{metric}")
    ResponseEntity<Map<String, Object>> getMetric(
            @PathVariable("metric") String metric
    );
}
