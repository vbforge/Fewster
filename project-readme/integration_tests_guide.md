# Integration Tests Guide - URL Shortener Application

## 2. Integration Tests

### 2.1 Controller Layer Testing - UrlRestController.java

#### 2.1.1 POST /api/v1/url
**Test: Create Single URL - Success Case**
- **Setup**: Valid URL (e.g., "https://www.example.com")
- **Request**: POST with `urlText` parameter
- **Expected**: HTTP 201 Created
- **Verify**: Response contains UrlDTO with id, originalUrl, and shortUrl
- **Verify**: Short URL format matches base URL prefix + generated code
- **Verify**: Database record is created

**Test: Create Single URL - Invalid URL Format**
- **Setup**: Invalid URLs (e.g., "not-a-url", "ftp://example.com", "", null)
- **Request**: POST with invalid `urlText` parameter
- **Expected**: HTTP 400 Bad Request
- **Verify**: No database record created
- **Verify**: Error response format matches GlobalExceptionHandler

**Test: Create Single URL - Duplicate URL**
- **Setup**: Create URL first time, then attempt to create same URL again
- **Request**: POST with same `urlText` parameter
- **Expected**: HTTP 201 Created (both times)
- **Verify**: Same shortUrl returned both times
- **Verify**: Only one database record exists
- **Verify**: Database timestamps reflect original creation time

**Test: Create Single URL - Database Error Scenario**
- **Setup**: Mock database failure
- **Request**: POST with valid URL
- **Expected**: HTTP 500 Internal Server Error
- **Verify**: Error response format
- **Verify**: No partial data corruption

#### 2.1.2 POST /api/v1/url/batch
**Test: Batch Create URLs - All Valid**
- **Setup**: List of valid URLs ["https://example1.com", "https://example2.com", "https://example3.com"]
- **Request**: POST with JSON array of URLs
- **Expected**: HTTP 201 Created
- **Verify**: Response contains array of UrlDTO objects
- **Verify**: All URLs have unique short URLs
- **Verify**: All database records created
- **Verify**: Response order matches request order

**Test: Batch Create URLs - Mixed Valid/Invalid**
- **Setup**: List with mix ["https://valid.com", "invalid-url", "https://another-valid.com"]
- **Request**: POST with mixed URL array
- **Expected**: HTTP 400 Bad Request
- **Verify**: No database records created (transaction rollback)
- **Verify**: Error message indicates which URL is invalid

**Test: Batch Create URLs - Some Duplicates**
- **Setup**: List with duplicates ["https://example.com", "https://new.com", "https://example.com"]
- **Request**: POST with URL array containing duplicates
- **Expected**: HTTP 201 Created
- **Verify**: Duplicate URLs return same shortUrl
- **Verify**: Only unique database records created
- **Verify**: Response contains all requested URLs with appropriate shortUrls

**Test: Batch Create URLs - Empty Array**
- **Setup**: Empty array []
- **Request**: POST with empty JSON array
- **Expected**: HTTP 201 Created
- **Verify**: Empty response array
- **Verify**: No database records created

**Test: Batch Create URLs - Large Batch**
- **Setup**: Array with 100+ valid URLs
- **Request**: POST with large URL array
- **Expected**: HTTP 201 Created
- **Verify**: All URLs processed successfully
- **Verify**: Response time within acceptable limits
- **Verify**: Database transaction completes successfully

#### 2.1.3 GET /api/v1/url/{id}
**Test: Get URL by ID - Existing Record**
- **Setup**: Create URL record, note the returned ID
- **Request**: GET /api/v1/url/{validId}
- **Expected**: HTTP 200 OK
- **Verify**: Response contains correct UrlDTO
- **Verify**: All fields match database record
- **Verify**: ID, originalUrl, and shortUrl are populated

**Test: Get URL by ID - Non-existing ID**
- **Setup**: Use ID that doesn't exist (e.g., 99999)
- **Request**: GET /api/v1/url/99999
- **Expected**: HTTP 404 Not Found
- **Verify**: Error response format matches GlobalExceptionHandler
- **Verify**: Error message indicates ID not found

**Test: Get URL by ID - Invalid ID Format**
- **Setup**: Use invalid ID formats
- **Request**: GET /api/v1/url/0, GET /api/v1/url/-1, GET /api/v1/url/abc
- **Expected**: HTTP 400 Bad Request
- **Verify**: Appropriate error message for invalid ID
- **Verify**: No database query executed for obviously invalid IDs

**Test: Get URL by ID - Database Error**
- **Setup**: Mock database failure
- **Request**: GET /api/v1/url/{validId}
- **Expected**: HTTP 500 Internal Server Error
- **Verify**: Error response format
- **Verify**: No data leakage in error response

#### 2.1.4 GET /api/v1/url/all
**Test: Get All URLs - Empty Database**
- **Setup**: Clean database (no URL records)
- **Request**: GET /api/v1/url/all
- **Expected**: HTTP 200 OK
- **Verify**: Response is empty array []
- **Verify**: Response format is JSON array

**Test: Get All URLs - Populated Database**
- **Setup**: Create multiple URL records (5-10 records)
- **Request**: GET /api/v1/url/all
- **Expected**: HTTP 200 OK
- **Verify**: Response contains all created URLs
- **Verify**: Each UrlDTO has complete data
- **Verify**: Count matches database record count

**Test: Get All URLs - Large Dataset**
- **Setup**: Create 100+ URL records
- **Request**: GET /api/v1/url/all
- **Expected**: HTTP 200 OK
- **Verify**: All records returned
- **Verify**: Response time within acceptable limits
- **Verify**: No memory issues with large response

**Test: Get All URLs - Database Error**
- **Setup**: Mock database failure
- **Request**: GET /api/v1/url/all
- **Expected**: HTTP 500 Internal Server Error
- **Verify**: Error response format
- **Verify**: Service handles exception gracefully

#### 2.1.5 GET /api/v1/url/short?shortUrl={shortUrl}
**Test: Get URL by Short URL - Existing Short URL**
- **Setup**: Create URL record, note the generated shortUrl
- **Request**: GET /api/v1/url/short?shortUrl={validShortUrl}
- **Expected**: HTTP 200 OK
- **Verify**: Response contains correct UrlDTO
- **Verify**: Original URL matches what was stored
- **Verify**: Short URL parameter matches response

**Test: Get URL by Short URL - Non-existing Short URL**
- **Setup**: Use short URL that doesn't exist
- **Request**: GET /api/v1/url/short?shortUrl=nonexistent
- **Expected**: HTTP 404 Not Found
- **Verify**: Error response indicates short URL not found
- **Verify**: Error message format matches GlobalExceptionHandler

**Test: Get URL by Short URL - Empty/Null Parameter**
- **Setup**: Use empty or missing shortUrl parameter
- **Request**: GET /api/v1/url/short?shortUrl= or GET /api/v1/url/short
- **Expected**: HTTP 400 Bad Request
- **Verify**: Error message indicates shortUrl parameter required
- **Verify**: No database query executed

**Test: Get URL by Short URL - Malformed Short URL**
- **Setup**: Use malformed short URL (special characters, too long, etc.)
- **Request**: GET /api/v1/url/short?shortUrl=malformed@url
- **Expected**: HTTP 400 Bad Request or HTTP 404 Not Found
- **Verify**: Appropriate error handling
- **Verify**: No security vulnerabilities exposed

### 2.2 Redirect Controller Testing - UrlRedirectRestController.java

#### 2.2.1 GET /r/{shortCode}
**Test: Successful Redirect - Valid Short Code**
- **Setup**: Create URL record, extract shortCode from shortUrl
- **Request**: GET /r/{validShortCode}
- **Expected**: HTTP 302 Found (redirect)
- **Verify**: Location header contains original URL
- **Verify**: Browser/client is redirected to original URL
- **Verify**: No response body (redirect response)

**Test: Redirect - Non-existing Short Code**
- **Setup**: Use short code that doesn't exist in database
- **Request**: GET /r/nonexistent
- **Expected**: HTTP 404 Not Found
- **Verify**: Error response with "Short URL not found" message
- **Verify**: No redirect occurs
- **Verify**: Error response format

**Test: Redirect - Malformed Short Code**
- **Setup**: Use various malformed short codes
- **Request**: GET /r/malformed@code, GET /r/toolongcode123456789
- **Expected**: HTTP 404 Not Found
- **Verify**: Appropriate error handling
- **Verify**: No security vulnerabilities
- **Verify**: No database errors from malformed input

**Test: Redirect - Database Error During Lookup**
- **Setup**: Mock database failure
- **Request**: GET /r/{validShortCode}
- **Expected**: HTTP 500 Internal Server Error
- **Verify**: Error response format
- **Verify**: No redirect occurs
- **Verify**: Error is logged appropriately

**Test: Redirect - URL Construction**
- **Setup**: Create URL with different base URL prefix configurations
- **Request**: GET /r/{shortCode}
- **Expected**: HTTP 302 Found
- **Verify**: Short code is correctly extracted from full shortUrl
- **Verify**: Base URL prefix is handled correctly
- **Verify**: Redirect works regardless of base URL configuration

### 2.3 Cross-Controller Integration Tests

#### 2.3.1 Complete URL Lifecycle Test
**Test: Create → Retrieve → Redirect Flow**
1. **Step 1**: POST /api/v1/url with original URL
2. **Step 2**: Verify 201 response with shortUrl
3. **Step 3**: GET /api/v1/url/{id} to retrieve record
4. **Step 4**: Verify record data consistency
5. **Step 5**: Extract shortCode from shortUrl
6. **Step 6**: GET /r/{shortCode} to test redirect
7. **Step 7**: Verify redirect to original URL
8. **Step 8**: GET /api/v1/url/short?shortUrl={shortUrl} to verify lookup
9. **Step 9**: Verify all responses are consistent

#### 2.3.2 Batch Processing Integration Test
**Test: Batch Create → Individual Retrieval → Redirects**
1. **Step 1**: POST /api/v1/url/batch with multiple URLs
2. **Step 2**: Verify all URLs created successfully
3. **Step 3**: For each returned URL, test individual retrieval by ID
4. **Step 4**: For each returned URL, test retrieval by shortUrl
5. **Step 5**: For each returned URL, test redirect functionality
6. **Step 6**: Verify GET /api/v1/url/all includes all created URLs

#### 2.3.3 Error Handling Integration Test
**Test: Cross-Controller Error Consistency**
1. **Step 1**: Test invalid URL creation
2. **Step 2**: Verify error format from UrlRestController
3. **Step 3**: Test redirect with non-existent short code
4. **Step 4**: Verify error format from UrlRedirectRestController
5. **Step 5**: Compare error response formats for consistency
6. **Step 6**: Verify GlobalExceptionHandler handles both controller errors

### 2.4 Database Integration Verification

#### 2.4.1 Transaction Testing
**Test: Database Transaction Rollback**
- **Setup**: Mock database failure during URL creation
- **Request**: POST /api/v1/url with valid URL
- **Expected**: HTTP 500 Internal Server Error
- **Verify**: No partial records in database
- **Verify**: Database state remains consistent

**Test: Batch Transaction Rollback**
- **Setup**: Create batch with one invalid URL in the middle
- **Request**: POST /api/v1/url/batch with mixed URLs
- **Expected**: HTTP 400 Bad Request
- **Verify**: No records created in database (full rollback)
- **Verify**: Database constraints enforced

#### 2.4.2 Concurrent Access Testing
**Test: Concurrent URL Creation**
- **Setup**: Multiple threads creating URLs simultaneously
- **Request**: Concurrent POST requests to /api/v1/url
- **Expected**: All requests succeed
- **Verify**: No duplicate short URLs generated
- **Verify**: All URLs stored correctly
- **Verify**: Database integrity maintained

**Test: Concurrent Redirects**
- **Setup**: Create URL, then multiple concurrent redirect requests
- **Request**: Concurrent GET requests to /r/{shortCode}
- **Expected**: All redirects succeed
- **Verify**: Database handles concurrent reads
- **Verify**: No performance degradation

### 2.5 Configuration Integration Testing

#### 2.5.1 Base URL Configuration Test
**Test: Different Base URL Configurations**
- **Setup**: Test with different `base.url.prefix` values
- **Request**: Create URL and test redirect
- **Expected**: Redirect works with any base URL configuration
- **Verify**: Short URLs are generated with correct prefix
- **Verify**: Redirect controller extracts short code correctly

#### 2.5.2 Short URL Length Configuration Test
**Test: Different Short URL Lengths**
- **Setup**: Test with different `short.url.length` values (4, 6, 8, 10)
- **Request**: Create URLs and verify short code length
- **Expected**: Generated short codes match configured length
- **Verify**: All short codes are unique
- **Verify**: Redirect functionality works with different lengths

### 2.6 Performance Integration Testing

#### 2.6.1 Response Time Testing
**Test: API Response Times**
- **Setup**: Database with existing records
- **Request**: Multiple API calls with timing
- **Expected**: Response times within acceptable limits
- **Verify**: URL creation < 200ms
- **Verify**: URL retrieval < 100ms
- **Verify**: Redirect < 50ms

#### 2.6.2 Database Performance Testing
**Test: Database Query Performance**
- **Setup**: Database with large dataset (10,000+ records)
- **Request**: Various API calls
- **Expected**: Performance remains acceptable
- **Verify**: Index usage for short URL and original URL lookups
- **Verify**: No significant performance degradation with dataset size