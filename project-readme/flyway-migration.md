### Database Migration

We use Flyway for database migrations:

```bash
# Run migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Clean database (development only)
mvn flyway:clean
```