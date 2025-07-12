# Fewster - URL Shortener Service

A modern URL shortening service built with Spring Boot, MySQL, and Docker. This project provides a complete solution for 
creating, managing, and tracking shortened URLs.

## Architecture

**Monolithic Architecture** - Single deployable unit containing:
- Spring Boot REST API backend
- MySQL database
- Static frontend (HTML/CSS/JavaScript)
- Redis for caching (optional)

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security
- **Database**: MySQL 8.0
- **Cache**: Redis (optional)
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **CI/CD**: Jenkins
- **Testing**: JUnit 5, Testcontainers

## Features

- ✅ URL shortening with custom aliases
- ✅ Click tracking and analytics
- ✅ URL expiration dates
- ✅ Basic authentication
- ✅ Rate limiting
- ✅ QR code generation
- ✅ Bulk URL shortening
- ✅ REST API with OpenAPI documentation
- ✅ Responsive web interface

## Project Structure

```
Fewster/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/vladproduction/fewster/
│   │   │       ├── FewsterApplication.java
│   │   │       ├── config/ --> not implemented yet
│   │   │       │   ├── SecurityConfig.java --> not implemented yet
│   │   │       │   ├── RedisConfig.java --> not implemented yet
│   │   │       │   └── OpenApiConfig.java --> not implemented yet
│   │   │       ├── controller/
│   │   │       │   ├── UrlRestController.java
│   │   │       │   ├── AnalyticsController.java --> not implemented yet 
│   │   │       │   ├── UrlController.java --> not implemented yet (ui)
│   │   │       │   ├── UrlRedirectController.java --> not implemented yet (ui)
│   │   │       │   └── UrlRedirectRestController.java
│   │   │       ├── service/
│   │   │       │   ├── UrlService.java
│   │   │       │   ├── ShortAlgorithmService.java
│   │   │       │   ├── AnalyticsService.java --> not implemented yet
│   │   │       │   └── QRCodeService.java --> not implemented yet
│   │   │       ├── repository/
│   │   │       │   ├── UrlRepository.java
│   │   │       │   └── ClickRepository.java --> not implemented yet
│   │   │       ├── model/
│   │   │       │   ├── Url.java
│   │   │       │   ├── Click.java --> not implemented yet
│   │   │       │   └── User.java --> not implemented yet
│   │   │       ├── dto/
│   │   │       │   ├── UrlRequest.java --> not implemented yet
│   │   │       │   ├── UrlResponse.java --> not implemented yet
│   │   │       │   └── AnalyticsResponse.java --> not implemented yet
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── CustomExceptions.java --> not implemented yet
│   │   │       └── util/
│   │   │           ├── Base62Encoder.java
│   │   │           └── ValidationUtils.java
│   │   └── resources/
│   │       ├── application.yml --> not implemented yet
│   │       ├── application-dev.yml --> not implemented yet
│   │       ├── application-prod.yml --> not implemented yet
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── db/migration/
│   │           ├── V1__create_url_table.sql
│   │           ├── V2__create_click_table.sql
│   │           └── V3__create_user_table.sql
│   └── test/
│       └── java/
│           └── com/vladproduction/fewster/
│               ├── integration/
│               ├── service/
│               └── repository/
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── docker-compose.prod.yml
│   └── mysql/
│       └── init.sql
├── jenkins/
│   ├── Jenkinsfile
│   └── deploy.sh
├── k8s/ (optional)
│   ├── deployment.yaml
│   ├── service.yaml
│   └── ingress.yaml
├── .github/
│   └── workflows/
│       └── ci.yml
├── pom.xml
├── README.md
├── .gitignore
└── .env.example
```

## Prerequisites

- Java 17 JDK
- Maven 3.8+
- Docker and Docker Compose
- MySQL 8.0 (if running locally)
- Redis (optional)
- Jenkins (for CI/CD)

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/url-shortener.git
cd url-shortener
```

### 2. Environment Setup

Copy the environment template:
```bash
cp .env.example .env
```

Edit `.env` file with your configurations:
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=fewsterdb
DB_USERNAME=fewster_user
DB_PASSWORD=your_secure_password

# Redis Configuration (optional)
REDIS_HOST=localhost
REDIS_PORT=6379

# Application Configuration
APP_PORT=8080
APP_BASE_URL=http://localhost:8080
JWT_SECRET=your_jwt_secret_key

# Rate Limiting
RATE_LIMIT_REQUESTS=100
RATE_LIMIT_WINDOW=3600

# QR Code Configuration
QR_CODE_SIZE=200
```

### 3. Database Setup

#### Option A: Using Docker (Recommended)
```bash
# Start MySQL and Redis with Docker Compose
docker-compose up -d mysql redis

# Wait for database to be ready
docker-compose logs -f mysql
```

#### Option B: Local MySQL Installation
```bash
# Create database
mysql -u root -p
CREATE DATABASE fewsterdb;
CREATE USER 'fewster_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON quicklink.* TO 'fewster_user'@'localhost';
FLUSH PRIVILEGES;
```

### 4. Build and Run

#### Development Mode
```bash
# Build the application
mvn clean install

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or run with Docker
docker-compose up -d
```

#### Production Mode
```bash
# Build production image
docker build -t fewster:latest .

# Run with production compose
docker-compose -f docker-compose.prod.yml up -d
```

### 5. Access the Application

- **Web Interface**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## API Endpoints

### URL Operations

```http
POST /api/v1/urls
Content-Type: application/json

{
  "originalUrl": "https://example.com/very-long-url",
  "customAlias": "mylink",
  "expiresAt": "2024-12-31T23:59:59"
}
```

```http
GET /api/v1/urls/{id}
GET /api/v1/urls/user/{userId}
PUT /api/v1/urls/{id}
DELETE /api/v1/urls/{id}
```

### Analytics

```http
GET /api/v1/analytics/url/{shortCode}
GET /api/v1/analytics/user/{userId}
```

### Redirection

```http
GET /{shortCode}
```

## Development Setup

### 1. IDE Configuration

**IntelliJ IDEA**:
- Install Spring Boot plugin
- Configure Java 17 SDK
- Enable annotation processing
- Set up code style (Google Java Style)

**VS Code**:
- Install Extension Pack for Java
- Install Spring Boot Extension Pack
- Configure Java 17 in settings

### 2. Database Migration

We use Flyway for database migrations:

```bash
# Run migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Clean database (development only)
mvn flyway:clean
```

### 3. Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn integration-test

# Run with coverage
mvn clean verify jacoco:report

# Run specific test
mvn test -Dtest=UrlServiceTest
```

### 4. Local Development with Hot Reload

```bash
# Run with dev profile and hot reload
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

## Docker Setup

### 1. Build Docker Image

```bash
# Build application image
docker build -t quicklink:latest .

# Build with specific profile
docker build --build-arg SPRING_PROFILES_ACTIVE=prod -t quicklink:prod .
```

### 2. Docker Compose Services

```yaml
# docker-compose.yml
version: '3.8'
services:
   app:
      build: ..
      ports:
         - "8080:8080"
      depends_on:
         - mysql
         - redis
      environment:
         - SPRING_PROFILES_ACTIVE=dev
         - DB_HOST=mysql
         - REDIS_HOST=redis

   mysql:
      image: mysql:8.0
      environment:
         MYSQL_ROOT_PASSWORD: root
         MYSQL_DATABASE: fewsterdb
      volumes:
         - mysql_data:/var/lib/mysql
      ports:
         - "3306:3306"

   redis:
      image: redis:7-alpine
      ports:
         - "6379:6379"
```

### 3. Multi-stage Docker Build

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## CI/CD with Jenkins

### 1. Jenkins Setup

```bash
# Install Jenkins with Docker
docker run -d -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkinsci/blueocean
```

### 2. Pipeline Configuration

Create `Jenkinsfile`:

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'fewster'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh './jenkins/deploy.sh'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
```

### 3. Deployment Script

```bash
#!/bin/bash
# jenkins/deploy.sh

set -e

echo "Deploying QuickLink application..."

# Stop existing containers
docker-compose down

# Pull latest images
docker-compose pull

# Start services
docker-compose up -d

# Wait for health check
echo "Waiting for application to be ready..."
until curl -f http://localhost:8080/actuator/health; do
    echo "Waiting..."
    sleep 5
done

echo "Deployment completed successfully!"
```

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

## Performance Optimization

### 1. Database Indexing

```sql
-- Create indexes for better performance
CREATE INDEX idx_url_short_code ON urls(short_code);
CREATE INDEX idx_url_user_id ON urls(user_id);
CREATE INDEX idx_url_created_at ON urls(created_at);
CREATE INDEX idx_click_url_id ON clicks(url_id);
CREATE INDEX idx_click_created_at ON clicks(created_at);
```

### 2. Redis Caching

```java
@Service
public class UrlService {
    
    @Cacheable(value = "urls", key = "#shortCode")
    public Url findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }
    
    @CacheEvict(value = "urls", key = "#result.shortCode")
    public Url createShortUrl(UrlRequest request) {
        // Implementation
    }
}
```

### 3. Rate Limiting

```java
@Component
public class RateLimitingFilter implements Filter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // Rate limiting implementation
    }
}
```

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

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check database connectivity
   docker-compose logs mysql
   
   # Reset database
   docker-compose down -v
   docker-compose up -d mysql
   ```

2. **Redis Connection Issues**
   ```bash
   # Check Redis logs
   docker-compose logs redis
   
   # Test Redis connection
   redis-cli ping
   ```

3. **Application Startup Issues**
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Check health endpoint
   curl http://localhost:8080/actuator/health
   ```

### Performance Issues

1. **Database Performance**
   ```sql
   -- Check slow queries
   SHOW FULL PROCESSLIST;
   
   -- Analyze table performance
   EXPLAIN SELECT * FROM urls WHERE short_code = 'abc123';
   ```

2. **Memory Issues**
   ```bash
   # Check memory usage
   docker stats
   
   # Adjust JVM memory settings
   JAVA_OPTS="-Xmx1g -Xms512m"
   ```

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

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue on GitHub
- Email: support@quicklink.com
- Documentation: https://docs.quicklink.com

---

**Happy URL Shortening! 🚀**