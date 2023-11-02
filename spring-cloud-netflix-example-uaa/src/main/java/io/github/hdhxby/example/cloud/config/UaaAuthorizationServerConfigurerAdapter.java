package io.github.hdhxby.example.cloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;

import java.util.List;

@Configuration
@EnableAuthorizationServer
public class UaaAuthorizationServerConfigurerAdapter extends AuthorizationServerConfigurerAdapter {

    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 30 * 60;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.withClientDetails()
        clients
                .inMemory()
                .withClient("uaa")
                .secret("secret")
                .autoApprove(false) // 自动批准
                .redirectUris("http://127.0.0.1:8080/uaa/api/authorization_code") //重定向uri
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "implicit", "client_credentials", "password", "refresh_token")
                .and()
                .withClient("hystrix")
                .secret("secret")
                .autoApprove(true) // 自动批准
                .redirectUris("http://127.0.0.1:8080/hystrix/api/authorization_code") //重定向uri
                .scopes("all")
                .authorizedGrantTypes("authorization_code", "implicit", "client_credentials", "password", "refresh_token");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //  开启/oauth/check_token验证端口认证权限访问，checkTokenAccess("isAuthenticated()")设置授权访问
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
                //允许表单认证
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenService())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }


    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }


    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);//客户端详情服务
        service.setSupportRefreshToken(true);//支持刷新令牌
        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天

        service.setTokenStore(jwtTokenStore());//令牌存储策略
//        // JWT需要设置Token解码器
//        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//        tokenEnhancerChain.setTokenEnhancers(List.of(jwtAccessTokenConverter()));
//        service.setTokenEnhancer(tokenEnhancerChain);

        return service;
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new InMemoryTokenStore();//JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("uaa");
        return jwtAccessTokenConverter;
    }

}
