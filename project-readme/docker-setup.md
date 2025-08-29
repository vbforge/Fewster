##  Docker rebuild instructions --> Recommended Workflow:
```bash
# Stop everything
docker-compose down

# Remove old app image
docker rmi fewster-app

# Rebuild and start
docker-compose up --build -d

# View logs
docker-compose logs -f app
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
    build: ../../../Fewster-process
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