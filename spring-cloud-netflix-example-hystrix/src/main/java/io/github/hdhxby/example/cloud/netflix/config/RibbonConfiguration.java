package io.github.hdhxby.example.cloud.netflix.config;

import io.github.hdhxby.example.cloud.netflix.loadbalancer.ThinkPing;
import io.github.hdhxby.example.cloud.netflix.loadbalancer.ThinkRule;
import com.netflix.loadbalancer.ILoadBalancer;
import org.springframework.context.annotation.Bean;

public class RibbonConfiguration {

    @Bean
    public ThinkRule thinkRule(ILoadBalancer loadBalancer){
        return new ThinkRule(loadBalancer);
    }

    @Bean
    public ThinkPing thinkPing(){
        return new ThinkPing();
    }
}
