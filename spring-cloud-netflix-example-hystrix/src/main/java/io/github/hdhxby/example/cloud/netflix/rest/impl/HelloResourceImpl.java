package io.github.hdhxby.example.cloud.netflix.rest.impl;

import io.github.hdhxby.example.cloud.netflix.rest.HelloResource;
import io.github.hdhxby.example.cloud.netflix.feign.HystrixdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
public class HelloResourceImpl implements HelloResource {

    private static final Logger logger = LoggerFactory.getLogger(HelloResourceImpl.class);

    private static final String WORD_API = "http://hystrix/api/world";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HystrixdClient hystrixdClient;

    @Override
    public ResponseEntity<String> hello(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis){
        return restTemplate.getForEntity(URI.create(String.format(WORD_API +"?name=%s&millis=%d",name,millis)),String.class);
    }

    @Override
    public ResponseEntity<String> world(@RequestParam(value = "name",defaultValue = "world",required = false) String name,@RequestParam(value = "millis",defaultValue = "0",required = false) Long millis){
        return hystrixdClient.world(name,millis);
    }
}
