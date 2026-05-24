# Backend Project Setup Guide

## Quick Start

### 1. Prerequisites
- Java 21 JDK installed
- Maven 3.8.1+
- PostgreSQL 13+

### 2. Project Structure

```
travel-trip-planning-backend/
├── src/
│   ├── main/
│   │   ├── java/com/travelplanner/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── FlightController.java
│   │   │   │   ├── TrainController.java
│   │   │   │   └── SearchController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── FlightService.java
│   │   │   │   ├── TrainService.java
│   │   │   │   ├── SearchHistoryService.java
│   │   │   │   └── PriceAlertService.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Flight.java
│   │   │   │   ├── Train.java
│   │   │   │   ├── SearchHistory.java
│   │   │   │   └── PriceAlert.java
│   │   │   ├── dto/
│   │   │   │   ├── FlightSearchRequest.java
│   │   │   │   ├── FlightSearchResponse.java
│   │   │   │   ├── TrainSearchRequest.java
│   │   │   │   ├── UserRegisterRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── SearchHistoryRepository.java
│   │   │   │   ├── PriceAlertRepository.java
│   │   │   │   ├── FlightCacheRepository.java
│   │   │   │   └── TrainCacheRepository.java
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── util/
│   │   │   │   ├── ApiClient.java
│   │   │   │   └── CacheUtil.java
│   │   │   └── TravelPlannerApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/travelplanner/
│           ├── service/
│           ├── controller/
│           └── repository/
├── pom.xml
└── database-schema.sql
```

### 3. Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE travel_db;
CREATE USER travel_user WITH PASSWORD 'travel_password';
ALTER ROLE travel_user SET client_encoding TO 'utf8';
ALTER ROLE travel_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE travel_user SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE travel_db TO travel_user;
```

2. Apply schema:
```bash
psql -U travel_user -d travel_db -f database-schema.sql
```

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# The application will start on http://localhost:8080
```

### 5. Development Setup

#### Application Properties
- Update `application.yml` with your configuration
- Set environment variables:
  ```bash
  export AMADEUS_API_KEY=your_key
  export AMADEUS_API_SECRET=your_secret
  export TRAIN_API_KEY=your_key
  ```

#### IDE Setup (IntelliJ IDEA)
1. Open project as Maven project
2. Enable annotation processing (Settings > Build > Compiler > Annotation Processors)
3. Set Java SDK to Java 21

#### IDE Setup (VS Code)
1. Install Extension Pack for Java
2. Open the project folder
3. Maven will auto-configure the project

### 6. Next Steps

1. Create core models (User, SearchHistory, etc.)
2. Create repositories
3. Implement authentication service
4. Create JWT token provider
5. Implement API integrations

---

## Technology Stack

- **Java**: 21 LTS
- **Spring Boot**: 3.2.0
- **Database**: PostgreSQL 13+
- **Build Tool**: Maven 3.8.1+
- **Security**: Spring Security + JWT

## Key Features Implemented

- ✅ Maven project structure
- ✅ Spring Boot 3.2+ configuration
- ✅ PostgreSQL database schema
- ✅ JWT security configuration
- ✅ CORS configuration
- ✅ Application properties

## Next Implementation Phase

- User model and repository
- Authentication service
- JWT token provider
- Flight/Train services
- Search history tracking
- Price alert system
