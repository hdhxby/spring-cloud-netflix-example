package io.github.hdhxby.example.cloud.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api")
@RestController
public class AuthorizationResource {

    @GetMapping("authorization_code")
    public ResponseEntity<String> authorizationCode() {
        return ResponseEntity.ok("authorization_code");
    }
}
