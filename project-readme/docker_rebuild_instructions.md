## Recommended Workflow:
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