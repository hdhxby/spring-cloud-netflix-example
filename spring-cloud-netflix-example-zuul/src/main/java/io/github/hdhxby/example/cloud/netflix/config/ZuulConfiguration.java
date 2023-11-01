package io.github.hdhxby.example.cloud.netflix.config;

import io.github.hdhxby.example.cloud.netflix.zuul.ZuulFallbackProvider;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Zuul配置
 * @author lixiaobin
 */
@Configuration
public class ZuulConfiguration {

    @Bean
    public FallbackProvider fallbackProvider() {
        return new ZuulFallbackProvider();
    }
}
