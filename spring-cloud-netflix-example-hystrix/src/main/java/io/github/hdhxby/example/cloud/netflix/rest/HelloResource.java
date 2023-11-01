package io.github.hdhxby.example.cloud.netflix.rest;

import io.github.hdhxby.example.cloud.netflix.entity.Hello;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
public interface HelloResource {

    @GetMapping("/hello")
    ResponseEntity<String> hello(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis);

    @GetMapping("/world")
    ResponseEntity<String> world(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis);
}
