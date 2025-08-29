## Monitoring and Metrics

### 1. Application Metrics

```yaml
# Prometheus configuration
management:
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: fewster
      environment: ${SPRING_PROFILES_ACTIVE}
```

### 2. Health Checks

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Custom health check logic
        return Health.up()
            .withDetail("database", "accessible")
            .withDetail("redis", "connected")
            .build();
    }
}
```