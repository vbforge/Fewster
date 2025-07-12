# README - Testing Guide for URL Shortener Application

## Overview
This document provides a comprehensive testing strategy for the URL shortener application. The application provides REST APIs for creating short URLs, retrieving URLs, and redirecting to original URLs.

## Test Categories

### 1. Unit Tests

#### 1.1 Utility Classes Testing
**AlgorithmUtility.java**
- Test `generateShortCode()` method with various URL inputs
- Test `generateSimpleShortCode()` as fallback mechanism
- Verify consistent output for same input URLs
- Test handling of special characters and edge cases
- Verify the generated code length matches configuration

**GlobalUtility.java**
- Test `isValidUrl()` method with valid and invalid URLs
- Test `generateUniqueShortUrl()` method for collision handling
- Test maximum attempt limit when generating unique URLs
- Verify URL validation logic for HTTP/HTTPS protocols

#### 1.2 Service Layer Testing
**UrlServiceImpl.java**
- Test `create()` method with valid URLs
- Test `create()` method with invalid URLs (should throw IllegalArgumentException)
- Test `create()` method with duplicate URLs (should return existing)
- Test `createAll()` method with mixed valid/invalid URLs
- Test `getById()` with valid and invalid IDs
- Test `getByShortUrl()` with existing and non-existing short URLs
- Test `getAll()` method for empty and populated database
- Test exception handling for all methods

**ShortAlgorithmServiceImpl.java**
- Test `makeShort()` method with various URL inputs
- Test null and empty string handling
- Verify base URL prefix is correctly applied

#### 1.3 Mapper Testing
**UrlMapper.java**
- Test `toDTO()` method with valid entity and null input
- Test `toEntity()` method with valid DTO and null input
- Verify all fields are correctly mapped between entity and DTO

#### 1.4 Repository Testing
**UrlRepository.java**
- Test `findByShortUrl()` method
- Test `findByOriginalUrl()` method
- Test `existsByShortUrl()` method
- Test database constraints (unique short URL, not null constraints)

### 2. Integration Tests

#### 2.1 Controller Layer Testing
**UrlRestController.java**

**POST /api/v1/url**
- Test successful URL creation with valid input
- Test invalid URL format handling
- Test duplicate URL handling
- Test empty/null input handling
- Verify HTTP status codes (201 for success, 400 for bad request)

**POST /api/v1/url/batch**
- Test batch creation with valid URL list
- Test batch creation with mixed valid/invalid URLs
- Test empty list handling
- Test large batch processing
- Verify response format and status codes

**GET /api/v1/url/{id}**
- Test retrieval with valid existing ID
- Test retrieval with non-existing ID
- Test invalid ID format (negative, zero)
- Verify HTTP status codes (200 for success, 404 for not found)

**GET /api/v1/url/all**
- Test retrieval of all URLs from empty database
- Test retrieval of all URLs from populated database
- Verify response format and pagination if implemented

**GET /api/v1/url/short?shortUrl={shortUrl}**
- Test retrieval with valid existing short URL
- Test retrieval with non-existing short URL
- Test invalid short URL format
- Verify HTTP status codes

#### 2.2 Redirect Controller Testing
**UrlRedirectRestController.java**

**GET /r/{shortCode}**
- Test successful redirect with valid short code
- Test redirect with non-existing short code
- Test malformed short code handling
- Verify HTTP redirect status codes (302 for redirect, 404 for not found)
- Test redirect URL correctness

### 3. End-to-End Tests

#### 3.1 Complete URL Shortening Flow
1. Create a short URL via POST /api/v1/url
2. Verify the short URL is returned correctly
3. Use the short URL to redirect via GET /r/{shortCode}
4. Verify redirect to original URL
5. Retrieve URL details via GET /api/v1/url/{id}
6. Verify all data consistency

#### 3.2 Batch Processing Flow
1. Create multiple URLs via POST /api/v1/url/batch
2. Verify all URLs are processed correctly
3. Test retrieval of all created URLs
4. Test individual redirects for each short URL

### 4. Performance Tests

#### 4.1 Load Testing
- Test concurrent URL creation requests
- Test concurrent redirect requests
- Measure response times under load
- Test database connection pool behavior

#### 4.2 Collision Testing
- Test short URL collision handling
- Verify unique URL generation under high concurrency
- Test maximum attempt limit behavior

### 5. Security Tests

#### 5.1 Input Validation
- Test SQL injection attempts in URL parameters
- Test XSS attempts in URL inputs
- Test malicious URL inputs
- Verify input sanitization

#### 5.2 URL Validation
- Test various URL formats and protocols
- Test extremely long URLs
- Test URLs with special characters
- Test internationalized domain names

### 6. Database Tests

#### 6.1 Data Integrity
- Test database constraints enforcement
- Test transaction rollback scenarios
- Test duplicate prevention at database level
- Verify timestamps (createdAt, updatedAt) are set correctly

#### 6.2 Index Performance
- Test query performance with large datasets
- Verify index usage for originalUrl and shortUrl lookups
- Test database performance under concurrent access

### 7. Configuration Tests

#### 7.1 Property Testing
- Test different `short.url.length` values
- Test different `base.url.prefix` configurations
- Test `generate.unique.short.url.maxAttempt` limits
- Test different `characters.string.literal` configurations

### 8. Error Handling Tests

#### 8.1 Exception Scenarios
- Test GlobalExceptionHandler with various exceptions
- Test database connection failures
- Test service layer exception propagation
- Verify error response format consistency

#### 8.2 Edge Cases
- Test with maximum URL length (2048 characters)
- Test with minimum valid URL length
- Test with special characters in URLs
- Test with international characters

### 9. API Documentation Tests

#### 9.1 Response Format Validation
- Verify JSON response structures match documentation
- Test error response formats
- Validate HTTP status codes for all scenarios

### 10. Monitoring and Logging Tests

#### 10.1 Logging Verification
- Verify appropriate log levels are used
- Test log message formats and content
- Verify sensitive data is not logged
- Test logging in error scenarios

## Testing Tools and Frameworks

### Recommended Testing Stack
- **JUnit 5** for unit testing
- **Mockito** for mocking dependencies
- **Spring Boot Test** for integration testing
- **TestContainers** for database integration tests
- **MockMvc** for API testing
- **JMeter** for performance testing

### Test Data Management
- Use test profiles for different environments
- Implement test data builders for consistent test data
- Use database rollback strategies for test isolation
- Consider using embedded databases for unit tests

### Test Execution Strategy
- Run unit tests first (fastest feedback)
- Run integration tests after unit tests pass
- Run performance tests separately
- Implement continuous integration pipeline

## Success Criteria

### Unit Tests
- Minimum 90% code coverage
- All edge cases covered
- Fast execution (< 10 seconds total)

### Integration Tests
- All API endpoints tested
- Database interactions verified
- Error scenarios covered

### Performance Tests
- Response time < 100ms for URL creation
- Response time < 50ms for redirect
- Handle 1000+ concurrent requests

### Security Tests
- No security vulnerabilities identified
- Input validation working correctly
- No data leakage in error responses

## Test Environment Setup

### Prerequisites
- Java 17+
- Spring Boot application
- Test database (H2 for unit tests, PostgreSQL/MySQL for integration)
- Mock external services if any

### Configuration
- Separate application.properties for testing
- Test-specific database configuration
- Logging configuration for tests
- Test data initialization scripts

## Conclusion

This testing strategy ensures comprehensive coverage of the URL shortener application, from individual components to complete user workflows. Focus on critical paths first, then expand to edge cases and performance scenarios. Regular execution of these tests will help maintain code quality and catch regressions early in the development process.