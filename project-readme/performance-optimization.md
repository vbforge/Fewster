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