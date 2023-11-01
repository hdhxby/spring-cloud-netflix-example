package io.github.hdhxby.example.cloud.netflix.feign;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HystrixdFallBack implements HystrixdClient {

    @Override
    public ResponseEntity<String> world(String name,Long millis) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(String.format("fallback %s,sleep %d millis.", name, millis));
    }
}