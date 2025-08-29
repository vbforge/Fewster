# Fewster - URL Shortener Service
A modern URL shortening service built with Spring Boot, MySQL, and Docker. 
This project provides a complete solution for creating, managing, and tracking shortened URLs.
---
## Tech Stack
- **Monolithic Architecture** - Single deployable unit containing
- **Backend**: Java 17, Maven 3.8+, Spring Boot 3.x, Spring Data JPA, Spring Security; Spring Boot REST API backend
- **Database**: MySQL 8.0
- **Cache**: Redis; for caching (optional)
- **Frontend**: HTML5, CSS3, JavaScript (ES6+); Static frontend (HTML/CSS/JavaScript)
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **CI/CD**: Jenkins
- **Testing**: JUnit 5, Testcontainers
---
## Features
- URL shortening with custom aliases
- Click tracking and analytics
- URL expiration dates
- Basic authentication
- Rate limiting
- QR code generation
- Bulk URL shortening
- REST API with OpenAPI documentation
- Responsive web interface
---
## Complete Backend Implementation
- RESTful APIs for URL operations
- JWT-based authentication
- Rate limiting to prevent abuse
- Click tracking and analytics
---
## Production-Ready Setup
- Docker containerization for isolated environments
- Jenkins CI/CD pipeline for automated deployment
- Flyway for managing database schema migrations
- Redis caching for high-speed performance
- Health checks and monitoring for reliability
---
## Security Features
- Input validation at all levels
- SQL injection prevention
- Rate limiting as a security measure
- HTTPS configuration for secure communication
---
## 📌 Next Steps
1. Implement the backend APIs to replace any frontend mock logic
2. Configure database schema and migrations via Flyway
3. Set up the Docker environment for seamless local development
4. Create and configure Jenkins CI/CD pipeline for deployment automation

---
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