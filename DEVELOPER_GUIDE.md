# Phase 2 Quick Reference Guide

## API Endpoint Summary

### Authentication Endpoints
```
POST   /api/auth/register          - Register new user (public)
POST   /api/auth/login              - Login user (public)
POST   /api/auth/refresh            - Refresh JWT token (requires auth)
POST   /api/auth/logout             - Logout user (requires auth)
```

### Flight Endpoints
```
GET    /api/flights/search          - Search flights (requires auth)
GET    /api/flights/{id}            - Get flight details (requires auth)
POST   /api/flights/filter          - Filter flights (requires auth)
```

### Train Endpoints
```
GET    /api/trains/search           - Search trains (requires auth)
GET    /api/trains/{id}             - Get train details (requires auth)
POST   /api/trains/filter           - Filter trains (requires auth)
```

### Search History Endpoints
```
GET    /api/searches/history        - Get user search history (requires auth)
GET    /api/searches/{id}           - Get specific search (requires auth)
DELETE /api/searches/{id}           - Delete search (requires auth)
POST   /api/searches/{id}/resync    - Re-execute search (requires auth)
```

### Price Alert Endpoints
```
POST   /api/alerts                  - Create price alert (requires auth)
GET    /api/alerts                  - Get user alerts (requires auth)
PUT    /api/alerts/{id}             - Update alert threshold (requires auth)
DELETE /api/alerts/{id}             - Delete alert (requires auth)
GET    /api/alerts/{id}/history     - Get alert history (requires auth)
```

---

## Authentication Flow

### 1. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!"
  }'
```

**Response (200 OK):** Same as register

### 3. Use Token in Authenticated Requests
```bash
curl -X GET http://localhost:8080/api/flights/search \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "fromLocation": "LAX",
    "toLocation": "JFK",
    "departureDate": "2024-02-15",
    "passengerCount": 1
  }'
```

---

## Flight Search Example

### Search Flights
```bash
curl -X GET "http://localhost:8080/api/flights/search?fromLocation=LAX&toLocation=JFK&departureDate=2024-02-15&passengerCount=1&cabin=economy" \
  -H "Authorization: Bearer <token>"
```

**Response (200 OK):**
```json
[
  {
    "flightId": "AF123",
    "airline": "Air France",
    "fromLocation": "LAX",
    "toLocation": "JFK",
    "departureTime": "2024-02-15T08:00:00",
    "arrivalTime": "2024-02-15T12:00:00",
    "duration": "4h 0m",
    "stops": 0,
    "price": 299.99,
    "currency": "USD",
    "cabin": "economy"
  }
]
```

### Filter Flights
```bash
curl -X POST "http://localhost:8080/api/flights/filter?maxPrice=500&maxStops=1" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "flightId": "AF123",
      "price": 299.99,
      "stops": 0
    }
  ]'
```

---

## Price Alert Management

### Create Alert
```bash
curl -X POST "http://localhost:8080/api/alerts?searchId=5&searchType=flight&targetPrice=250.00" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "searchId": 5,
  "searchType": "flight",
  "targetPrice": 250.00,
  "isActive": true,
  "alertTriggered": false,
  "createdAt": "2024-02-01T10:30:00"
}
```

### Get Active Alerts
```bash
curl -X GET "http://localhost:8080/api/alerts?active=true" \
  -H "Authorization: Bearer <token>"
```

### Update Alert
```bash
curl -X PUT "http://localhost:8080/api/alerts/1?newTargetPrice=200.00" \
  -H "Authorization: Bearer <token>"
```

### Delete Alert
```bash
curl -X DELETE "http://localhost:8080/api/alerts/1" \
  -H "Authorization: Bearer <token>"
```

---

## Search History

### Get Search History (Paginated)
```bash
curl -X GET "http://localhost:8080/api/searches/history?page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "searchType": "flight",
      "fromLocation": "LAX",
      "toLocation": "JFK",
      "departureDate": "2024-02-15",
      "returnDate": null,
      "passengerCount": 1,
      "searchResults": "...",
      "createdAt": "2024-02-01T10:30:00"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}
```

### Get Specific Search
```bash
curl -X GET "http://localhost:8080/api/searches/1" \
  -H "Authorization: Bearer <token>"
```

### Delete Search
```bash
curl -X DELETE "http://localhost:8080/api/searches/1" \
  -H "Authorization: Bearer <token>"
```

---

## Error Responses

### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2024-02-01T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "message": "Field errors",
  "path": "/api/auth/register",
  "details": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid"
  }
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-02-01T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-02-01T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found with id: 999",
  "path": "/api/searches/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-02-01T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An internal server error occurred",
  "path": "/api/flights/search"
}
```

---

## Common HTTP Status Codes

| Status | Meaning | Example |
|--------|---------|---------|
| 200 | OK | Login successful, search returned results |
| 201 | Created | User registered, alert created |
| 400 | Bad Request | Invalid input, validation failed |
| 401 | Unauthorized | Invalid token, invalid credentials |
| 404 | Not Found | Search not found, user not found |
| 500 | Server Error | Database error, external API failure |

---

## Key Code Examples

### Using JwtTokenProvider
```java
// Generate token
String token = jwtTokenProvider.generateToken(user);

// Validate token
if (jwtTokenProvider.validateToken(token)) {
    Long userId = jwtTokenProvider.getUserIdFromToken(token);
    String username = jwtTokenProvider.getUsernameFromToken(token);
}
```

### Using AuthService
```java
// Register user
AuthResponse response = authService.register(registerRequest);

// Login
AuthResponse response = authService.login(loginRequest);

// Refresh token
AuthResponse newResponse = authService.refreshToken(oldToken);
```

### Using FlightService
```java
// Search flights (with caching)
List<FlightSearchResponse> flights = flightService.searchFlights(searchRequest);
```

### Using SearchHistoryService
```java
// Save search
SearchHistory history = searchHistoryService.saveSearch(
    userId, "flight", "LAX", "JFK", 
    LocalDate.of(2024, 2, 15), null, 
    1, flightResults
);

// Get history
Page<SearchHistory> history = searchHistoryService.getUserSearchHistory(userId, 0, 10);

// Get specific search
SearchHistory search = searchHistoryService.getSearchById(searchId);
```

### Using PriceAlertService
```java
// Create alert
PriceAlert alert = priceAlertService.createAlert(
    userId, searchId, "flight", new BigDecimal("250.00")
);

// Get user alerts
List<PriceAlert> alerts = priceAlertService.getUserAlerts(userId, true);

// Update alert
PriceAlert updated = priceAlertService.updateAlert(alertId, new BigDecimal("200.00"));
```

---

## Configuration Properties

### JWT Configuration
```yaml
spring:
  security:
    jwt:
      secret: your-secret-key-change-this-in-production
      expiration: 86400000  # 24 hours
```

### API Configuration
```yaml
api:
  flight:
    amadeus:
      base-url: https://test.api.amadeus.com
      api-key: ${AMADEUS_API_KEY}
      api-secret: ${AMADEUS_API_SECRET}
  train:
    base-url: https://api.example.com
    api-key: ${TRAIN_API_KEY}

cache:
  flight:
    ttl-hours: 24
  train:
    ttl-hours: 24
```

### CORS Configuration
```yaml
spring:
  web:
    cors:
      allowed-origins: "http://localhost:3000,http://localhost:5173"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: true
```

---

## Troubleshooting

### Token Expired
- **Error**: `Invalid or expired token`
- **Solution**: Call `/api/auth/refresh` with the old token to get a new one

### Duplicate Username
- **Error**: `Username already exists`
- **Solution**: Choose a different username or login with existing credentials

### Invalid Credentials
- **Error**: `Invalid username or password`
- **Solution**: Verify username and password are correct

### Cache Not Working
- **Error**: Repeated API calls despite caching
- **Solution**: Check cache TTL configuration in application.yml

### CORS Errors
- **Error**: `Access to XMLHttpRequest blocked by CORS policy`
- **Solution**: Verify frontend origin is in allowed-origins

---

## Development Tips

1. **Always include Authorization header** for protected endpoints
2. **Use pagination** when fetching search history
3. **Check status codes** to understand response type
4. **Monitor logs** at DEBUG level for troubleshooting
5. **Cache results** are returned within 24 hours
6. **Test alerts** with `/api/alerts/check` (when implemented)

---

## Next Steps for Developers

1. Implement OAuth2 in AmadeusApiClient for production API calls
2. Complete user ID extraction from JWT in all controllers
3. Add comprehensive error handling for external API calls
4. Implement real price checking in PriceAlertService
5. Add token blacklist for server-side logout
6. Set up notification system for price alerts
7. Add advanced filtering and sorting options
8. Implement user preferences and recommendations

---

## Support & Resources

- **JWT Documentation**: https://github.com/jwtk/jjwt
- **Spring Security**: https://spring.io/projects/spring-security
- **Amadeus API**: https://developers.amadeus.com/
- **Project Documentation**: See PHASE_2_IMPLEMENTATION.md

