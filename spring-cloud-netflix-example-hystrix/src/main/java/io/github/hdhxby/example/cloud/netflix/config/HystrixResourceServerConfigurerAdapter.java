package io.github.hdhxby.example.cloud.netflix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class HystrixResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
         super.configure(http); // 这句注释掉启动会报错
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer clients) throws Exception {
        clients.resourceId("hystrix")
                .tokenServices(tokenService())
                .stateless(true);
    }

    @Bean
    public ResourceServerTokenServices tokenService() {
        RemoteTokenServices service=new RemoteTokenServices();
        service.setCheckTokenEndpointUrl("http://localhost:9999/oauth/check_token");
        service.setClientId("hystrix");
        service.setClientSecret("secret");
        return service;
    }

//    @Bean
//    public TokenStore jwtTokenStore() {
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }
//
//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setSigningKey("uaa");
//        return jwtAccessTokenConverter;
//    }

}
