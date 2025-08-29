## Production Deployment

### 1. Environment Variables

```bash
# Production environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=prod-mysql-host
export DB_PASSWORD=secure_prod_password
export JWT_SECRET=secure_jwt_secret
export REDIS_HOST=prod-redis-host
```

### 2. SSL Configuration

```yaml
# application-prod.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
```

### 3. Monitoring and Logging

```yaml
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.vladproduction.fewster: INFO
  file:
    name: /var/log/fewster/application.log
```