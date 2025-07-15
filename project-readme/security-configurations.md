## Security Configuration

### 1. JWT Authentication

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/{shortCode}").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );
        return http.build();
    }
}
```

### 2. Input Validation

```java
@RestController
@RequestMapping("/api/v1/urls")
@Validated
public class UrlController {
    
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody UrlRequest request) {
        // Implementation
    }
}
```