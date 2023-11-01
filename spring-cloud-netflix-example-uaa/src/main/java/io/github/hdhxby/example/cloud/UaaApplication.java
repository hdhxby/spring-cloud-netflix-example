package io.github.hdhxby.example.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置
 * @author lixiaobin
 * @version 2.0.0
 * @since 2.0.0
 */
@EnableDiscoveryClient
@SpringBootApplication
public class UaaApplication {

    private static final Logger logger = LoggerFactory.getLogger(UaaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }
}
