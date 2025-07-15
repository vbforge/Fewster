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

#### Option A: Using Docker
```bash
# Start MySQL and Redis with Docker Compose
docker-compose up -d mysql redis

# Wait for database to be ready
docker-compose logs -f mysql
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