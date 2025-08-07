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

                    // Public endpoints - no authentication required
                    authorize.requestMatchers(HttpMethod.GET, "/").permitAll(); // Homepage
                    authorize.requestMatchers(HttpMethod.GET, "/login").permitAll(); // Login page
                    authorize.requestMatchers(HttpMethod.GET, "/register").permitAll(); // Registration page
                    authorize.requestMatchers(HttpMethod.POST, "/register").permitAll(); // Registration form submission
                    authorize.requestMatchers(HttpMethod.GET, "/r/**").permitAll(); // Public redirects
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/demo-url").permitAll(); // Demo URL creation API
                    authorize.requestMatchers(HttpMethod.POST, "/demo-create").permitAll(); // Demo URL creation web form
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll(); // User registration API

                    // Static resources
                    authorize.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll();

                    // Protected dashboard endpoints - require authentication
                    authorize.requestMatchers(HttpMethod.GET, "/dashboard/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.POST, "/dashboard/**").hasRole(role);

                    // Protected URL management API endpoints - require authentication
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.PUT, "/api/v1/url/**").hasRole(role);
                    authorize.requestMatchers(HttpMethod.DELETE, "/api/v1/url/**").hasRole(role);

                    // All other requests require authentication
                    authorize.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .loginProcessingUrl("/login") // Login form submission URL
                        .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard after successful login
                        .failureUrl("/login?error=true") // Redirect to login page with error parameter
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Logout URL
                        .logoutSuccessUrl("/?logout=true") // Redirect to homepage after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete session cookie
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults()); // Keep HTTP Basic for API access

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
