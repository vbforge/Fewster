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