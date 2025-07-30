package com.vladproduction.fewster.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Value("${role.name}")
    private String role;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests((authorize) -> {
                    // Public endpoints
                    authorize.requestMatchers(HttpMethod.GET, "/r/**").permitAll(); // Public redirects
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/demo-url").permitAll(); // Public redirects
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll(); // User registration

                    // Protected URL management endpoints
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.PUT, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/url/**").hasRole(role);

                    // All other requests require authentication
                    authorize.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
