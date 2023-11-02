package io.github.hdhxby.example.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.authentication.AuthenticationManager;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class UaaWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);

        http
                .csrf(csrf -> csrf.disable())
//                .sessionManagement(sessionManaement -> sessionManaement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers("/").permitAll()
                        .antMatchers("/index").permitAll()
                        .antMatchers("/login","/logout").permitAll()
                        .antMatchers("/oauth/**").permitAll()
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin()
//                .loginPage("/login.html")
//                .loginProcessingUrl("/login").permitAll()
                .and()
                .httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(User.withUsername("user")
                .password("password")
                .roles("USER")
                .authorities("users")
                .build());
        userDetailsManager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("password"))
                .authorities("users")
                .roles("ADMIN").build());
        return userDetailsManager;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
