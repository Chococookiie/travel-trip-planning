# Travel Trip Planning Application - Implementation Plan

**Project**: Travel Trip Planning Application  
**Created**: 2026-05-23  
**Status**: Planning Phase  
**Version**: 1.0

---

## Executive Summary

Build a comprehensive travel planning application starting with a flight and train search functionality that integrates with real-time APIs (where available for free), with features to save searches and receive price drop alerts.

**MVP Scope**: Flight & Train Search → Save Searches → Price Alerts → User Authentication

---

## Phase 1: Foundation & MVP (Weeks 1-4)

### Goals
- Set up development environment and project structure
- Implement flight & train search APIs
- Build basic UI for search
- Implement user authentication
- Save search functionality with price alerts

### Deliverables
1. Project structure with Spring Boot backend
2. React frontend with search interface
3. PostgreSQL database schema
4. API integrations (Flights & Trains)
5. User authentication system
6. Search history and price alert system

---

## Detailed Implementation Breakdown

### Phase 1.1: Backend Setup (Week 1)

#### 1.1.1 Project Structure
```
travel-trip-planning-backend/
├── src/main/java/com/travelplanner/
│   ├── config/                    # Spring configurations
│   ├── controller/
│   │   ├── FlightController.java
│   │   ├── TrainController.java
│   │   ├── UserController.java
│   │   └── SearchController.java
│   ├── service/
│   │   ├── FlightService.java
│   │   ├── TrainService.java
│   │   ├── UserService.java
│   │   ├── SearchHistoryService.java
│   │   └── PriceAlertService.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Flight.java
│   │   ├── Train.java
│   │   ├── SearchHistory.java
│   │   ├── PriceAlert.java
│   │   └── dto/
│   │       ├── FlightSearchRequest.java
│   │       ├── TrainSearchRequest.java
│   │       ├── FlightSearchResponse.java
│   │       └── TrainSearchResponse.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── SearchHistoryRepository.java
│   │   └── PriceAlertRepository.java
│   ├── util/
│   │   ├── ApiIntegrationUtil.java
│   │   └── PriceAlertUtil.java
│   └── TravelPlannerApplication.java
├── pom.xml
└── application.yml
```

#### 1.1.2 Java 21 LTS & Maven Configuration

**Java Version Setup**:
- Java 21 LTS (Latest long-term support)
- Spring Boot 3.2+ (Required for Java 21)
- Maven 3.8.1+

**pom.xml Configuration**:
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.travelplanner</groupId>
    <artifactId>travel-planner-backend</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>

        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.11.5</version>
        </dependency>

        <!-- HTTP Client -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 1.1.3 Database Schema
```sql
-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Search History Table
CREATE TABLE search_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    search_type VARCHAR(50) NOT NULL, -- 'flight' or 'train'
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    return_date DATE,
    passenger_count INT NOT NULL DEFAULT 1,
    search_results JSONB, -- Store API results as JSON
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Price Alerts Table
CREATE TABLE price_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    search_id BIGINT NOT NULL REFERENCES search_history(id),
    search_type VARCHAR(50) NOT NULL, -- 'flight' or 'train'
    target_price DECIMAL(10, 2),
    current_lowest_price DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    alert_triggered BOOLEAN DEFAULT FALSE,
    triggered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Flight Cache (to reduce API calls)
CREATE TABLE flight_cache (
    id BIGSERIAL PRIMARY KEY,
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    data JSONB NOT NULL,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(from_location, to_location, departure_date)
);

-- Train Cache (to reduce API calls)
CREATE TABLE train_cache (
    id BIGSERIAL PRIMARY KEY,
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    data JSONB NOT NULL,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(from_location, to_location, departure_date)
);
```

### Phase 1.2: API Integration (Week 1-2)

#### 1.2.1 Flight APIs (Free Options)
1. **Skyscanner (Free tier available)**
   - Endpoint: `https://skyscanner-api.p.rapidapi.com/`
   - Rate limit: 500 calls/month free tier
   - Features: Search flights, get prices, compare

2. **Amadeus Developer API (Free tier)**
   - Endpoint: `https://test.api.amadeus.com/`
   - Features: Flight search, inspiration search
   - Rate limit: 10 requests/second, 3,000 calls/month
   - **RECOMMENDED for MVP**

3. **Rapid API - Flight aggregators**
   - Multiple flight APIs available
   - Pricing: Free tier with limited requests

#### 1.2.2 Train APIs (Free Options)
1. **Open Trip Planner (Free, Open Source)**
   - Endpoint: Custom deployable API
   - Features: Multi-modal transit search
   
2. **Hyperdia (Limited free)**
   - Primarily for Asian railways

3. **Alternative: Web Scraping (with legal compliance)**
   - Perron (European trains)
   - Omio (Train + Bus + Flight)

#### 1.2.3 Implementation Strategy
```java
// FlightService.java
@Service
public class FlightService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private FlightCacheRepository flightCacheRepository;
    
    public List<Flight> searchFlights(FlightSearchRequest request) {
        // Check cache first
        FlightCache cachedResult = flightCacheRepository.findByLocationsAndDate(
            request.getFromLocation(),
            request.getToLocation(),
            request.getDepartureDate()
        );
        
        if (cachedResult != null && !cachedResult.isExpired()) {
            return deserializeFlights(cachedResult.getData());
        }
        
        // Call external API
        List<Flight> flights = callExternalFlightAPI(request);
        
        // Cache results (24 hours)
        cacheFlightResults(request, flights);
        
        return flights;
    }
    
    private List<Flight> callExternalFlightAPI(FlightSearchRequest request) {
        // Implementation for API calls
        // Handle Amadeus, Skyscanner, etc.
    }
}
```

### Phase 1.3: User Authentication (Week 1)

#### 1.3.1 Authentication Implementation
```java
// UserController.java
@RestController
@RequestMapping("/api/auth")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        String token = generateJWT(user);
        return ResponseEntity.ok(new AuthResponse(token, user));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getUsername(), request.getPassword());
        String token = generateJWT(user);
        return ResponseEntity.ok(new AuthResponse(token, user));
    }
}
```

### Phase 1.4: Search & History APIs (Week 2)

#### 1.4.1 Search Controller
```java
// SearchController.java
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private TrainService trainService;
    
    @Autowired
    private SearchHistoryService searchHistoryService;
    
    @PostMapping("/flights")
    public ResponseEntity<List<Flight>> searchFlights(
        @RequestBody FlightSearchRequest request,
        @AuthenticationPrincipal User user
    ) {
        List<Flight> results = flightService.searchFlights(request);
        
        // Save to search history
        searchHistoryService.saveSearch(user, "flight", request, results);
        
        return ResponseEntity.ok(results);
    }
    
    @PostMapping("/trains")
    public ResponseEntity<List<Train>> searchTrains(
        @RequestBody TrainSearchRequest request,
        @AuthenticationPrincipal User user
    ) {
        List<Train> results = trainService.searchTrains(request);
        
        // Save to search history
        searchHistoryService.saveSearch(user, "train", request, results);
        
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<SearchHistory>> getSearchHistory(
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(searchHistoryService.getUserSearchHistory(user.getId()));
    }
}
```

### Phase 1.5: Price Alert System (Week 2)

#### 1.5.1 Price Alert Service
```java
// PriceAlertService.java
@Service
public class PriceAlertService {
    
    @Autowired
    private PriceAlertRepository priceAlertRepository;
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private TrainService trainService;
    
    @Autowired
    private EmailService emailService;
    
    @Scheduled(fixedDelay = 3600000) // Run every hour
    public void checkPriceAlerts() {
        List<PriceAlert> activeAlerts = priceAlertRepository.findByIsActiveTrue();
        
        for (PriceAlert alert : activeAlerts) {
            SearchHistory search = alert.getSearch();
            
            if ("flight".equals(search.getSearchType())) {
                List<Flight> flights = flightService.searchFlights(
                    new FlightSearchRequest(search)
                );
                checkAndNotify(alert, flights, "flight");
            } else if ("train".equals(search.getSearchType())) {
                List<Train> trains = trainService.searchTrains(
                    new TrainSearchRequest(search)
                );
                checkAndNotify(alert, trains, "train");
            }
        }
    }
    
    private void checkAndNotify(PriceAlert alert, List<?> results, String type) {
        BigDecimal lowestPrice = getLowestPrice(results);
        
        if (lowestPrice.compareTo(alert.getTargetPrice()) <= 0) {
            alert.setAlertTriggered(true);
            alert.setTriggeredAt(LocalDateTime.now());
            priceAlertRepository.save(alert);
            
            // Send email notification
            emailService.sendPriceAlertEmail(alert.getUser(), alert, lowestPrice);
        }
    }
}
```

### Phase 1.6: Frontend Setup (Week 2-3)

#### 1.6.1 React Project Structure
```
travel-trip-planning-frontend/
├── src/
│   ├── components/
│   │   ├── SearchForm.jsx
│   │   ├── FlightResults.jsx
│   │   ├── TrainResults.jsx
│   │   ├── SearchHistory.jsx
│   │   ├── PriceAlerts.jsx
│   │   └── Navigation.jsx
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── SearchResults.jsx
│   │   ├── UserProfile.jsx
│   │   └── Login.jsx
│   ├── services/
│   │   ├── api.js
│   │   └── auth.js
│   ├── redux/
│   │   ├── slices/
│   │   │   ├── searchSlice.js
│   │   │   ├── userSlice.js
│   │   │   └── alertSlice.js
│   │   └── store.js
│   ├── styles/
│   │   ├── App.css
│   │   └── components/
│   └── App.jsx
└── package.json
```

#### 1.6.2 Key Components

**SearchForm Component**
- Input fields: From Location, To Location
- Input fields: Departure Date, Return Date
- Input field: Passenger Count
- Toggle: Search Flights/Trains
- Submit Button

**Results Component**
- Display list of available options
- Sort by: Price, Duration, Departure Time
- Filter options
- "Save Search" button
- "Set Price Alert" button

**Authentication**
- Login/Register forms
- JWT token management
- Protected routes

### Phase 1.7: Deployment & DevOps (Week 3-4)

#### 1.7.1 Docker Setup
```dockerfile
# Backend Dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/travel-planner.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# Frontend Dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

#### 1.7.2 Docker Compose
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: travel_db
      POSTGRES_USER: travel_user
      POSTGRES_PASSWORD: secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./travel-trip-planning-backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/travel_db
      - SPRING_DATASOURCE_USERNAME=travel_user
      - SPRING_DATASOURCE_PASSWORD=secure_password

  frontend:
    build: ./travel-trip-planning-frontend
    ports:
      - "3000:3000"
    depends_on:
      - backend
    environment:
      - REACT_APP_API_BASE_URL=http://localhost:8080

volumes:
  postgres_data:
```

---

## Development Workflow

### Week 1 Tasks
- [ ] Backend Initialization
  - [ ] Set up Spring Boot 3.2+ project structure with Java 21
  - [ ] Create PostgreSQL database schema
  - [ ] Implement user authentication (JWT)
  - [ ] Create User entity and repository
  - [ ] Set up application configuration files

- [ ] API Integration Preparation
  - [ ] Research and select flight/train APIs
  - [ ] Obtain API keys (Amadeus recommended)
  - [ ] Create API client utilities
  - [ ] Set up error handling for APIs

### Week 2 Tasks
- [ ] Flight & Train Services
  - [ ] Implement flight search service with caching
  - [ ] Implement train search service with caching
  - [ ] Add caching mechanism (24-hour TTL)
  - [ ] Error handling and fallbacks

- [ ] Search & History
  - [ ] Implement search history saving
  - [ ] Create search history endpoints
  - [ ] Implement price alert logic
  - [ ] Set up scheduled price alert checks (hourly)

### Week 3 Tasks
- [ ] Frontend Development
  - [ ] Set up React 18+ with TypeScript and Vite
  - [ ] Create search form component
  - [ ] Create results display components
  - [ ] Implement authentication UI
  - [ ] Set up Redux for state management

- [ ] Integration Testing
  - [ ] Test backend endpoints
  - [ ] Test frontend-backend communication
  - [ ] Integration testing between services

### Week 4 Tasks
- [ ] Deployment
  - [ ] Docker containerization for backend and frontend
  - [ ] Docker Compose setup for local development
  - [ ] GitHub Actions CI/CD pipeline setup
  - [ ] Deployment to AWS/Azure

- [ ] Testing & Optimization
  - [ ] Performance testing
  - [ ] Security review
  - [ ] Load testing
  - [ ] Bug fixes and optimizations

---

## API Endpoints (MVP)

### Authentication
```
POST   /api/auth/register              - Register user
POST   /api/auth/login                 - Login user
POST   /api/auth/refresh-token         - Refresh JWT token
```

### Search
```
POST   /api/search/flights             - Search flights
POST   /api/search/trains              - Search trains
GET    /api/search/history             - Get user search history
DELETE /api/search/history/{historyId} - Delete search history
```

### Price Alerts
```
POST   /api/alerts                     - Create price alert
GET    /api/alerts                     - Get user alerts
PUT    /api/alerts/{alertId}           - Update alert
DELETE /api/alerts/{alertId}           - Delete alert
```

---

## Technology Stack (Confirmed)

**Backend**: Java 21 LTS with Spring Boot 3.2+
**Frontend**: React 18+ with TypeScript, Vite
**Database**: PostgreSQL 13+
**Caching**: Database-level caching (future: Redis)
**Deployment**: Docker, Kubernetes (future), GitHub Actions
**Cloud**: AWS (recommended)

**Why Java 21**:
- Virtual threads for lightweight concurrency
- Pattern matching improvements
- Record classes for better DTOs
- Sealed classes for domain modeling
- LTS until September 2031
- Excellent performance for high-concurrency apps

---

## API Integration Costs

### Free Tier Analysis

| API | Free Tier | Rate Limit | Best For |
|-----|-----------|-----------|----------|
| **Amadeus** | 3,000 calls/month | 10/sec | **MVP Production** |
| Skyscanner | 500 calls/month | 10/min | Testing |
| Rapid API Aggregators | 100-500/month | Variable | Testing |
| Open Trip Planner | Unlimited (self-hosted) | N/A | Trains (complex) |

**Recommendation**: Start with Amadeus (3,000/month is sufficient for MVP) + fallback to cached data

---

## Success Metrics

- ✅ Users can search flights and trains simultaneously
- ✅ Search results display within 3 seconds
- ✅ Users can save searches and set price alerts
- ✅ Price alerts trigger when target is reached
- ✅ 99% API uptime (with fallbacks and caching)
- ✅ Less than 100ms response time for cached results

---

## Phase 2 Roadmap (Future)

- [ ] Redis caching layer for better performance
- [ ] Hotel booking integration
- [ ] Travel package recommendations (ML)
- [ ] Multi-modal trip planning (flights + trains + hotels)
- [ ] Mobile app (React Native)
- [ ] Machine learning for price predictions
- [ ] User reviews and ratings
- [ ] Group trip planning
- [ ] Notification system (email, SMS, push)
- [ ] Payment integration

---

## Risks & Mitigation

| Risk | Impact | Mitigation |
|------|--------|-----------|
| API rate limits exceeded | High | Implement caching, queue requests, use multiple APIs |
| API downtime | High | Fallback to cached data, show last known prices |
| Database scaling issues | Medium | Use connection pooling, implement read replicas |
| Security vulnerabilities | High | Regular security audits, OWASP compliance |
| Performance degradation | Medium | Implement caching, optimize queries, load testing |
| Free API quota exhaustion | Medium | Implement better caching, consider paid tier |

---

## Local Development Setup

### Prerequisites
- Java 21 JDK
- Maven 3.8.1+
- PostgreSQL 13+
- Node.js 16+
- Docker & Docker Compose (optional)

### Quick Start
```bash
# Clone repository
git clone https://github.com/Chococookiie/travel-trip-planning.git
cd travel-trip-planning

# Backend setup
cd travel-trip-planning-backend
mvn clean install
mvn spring-boot:run

# Frontend setup (in new terminal)
cd ../travel-trip-planning-frontend
npm install
npm run dev

# Or use Docker Compose
docker-compose up -d
```

---

## Revision History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-05-23 | Initial comprehensive implementation plan with Java 21 LTS |

---

## Next Steps

1. ✅ Review and approve this plan
2. Create GitHub issues for each task (breaking down by week)
3. Set up development branches (feature branches per task)
4. Begin Phase 1.1 implementation (Backend setup)
5. Set up GitHub Actions for CI/CD

**Questions or modifications needed?** Please provide feedback!
