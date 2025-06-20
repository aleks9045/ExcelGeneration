package org.example.server.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Aleksey
 */
@Endpoint(id = "test")
@Component
public class TestEndpoint {

    @ReadOperation
    public ResponseEntity<String> getString() {
        return ResponseEntity.ok("hello");
    }
}
