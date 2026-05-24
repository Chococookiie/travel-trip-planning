-- Travel Planner Database Schema for PostgreSQL

-- Create database
-- CREATE DATABASE travel_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Search History Table
CREATE TABLE IF NOT EXISTS search_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    search_type VARCHAR(50) NOT NULL CHECK (search_type IN ('flight', 'train')),
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    return_date DATE,
    passenger_count INT NOT NULL DEFAULT 1,
    search_results JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_search (user_id, created_at DESC)
);

-- Price Alerts Table
CREATE TABLE IF NOT EXISTS price_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    search_id BIGINT NOT NULL REFERENCES search_history(id) ON DELETE CASCADE,
    search_type VARCHAR(50) NOT NULL CHECK (search_type IN ('flight', 'train')),
    target_price DECIMAL(10, 2) NOT NULL,
    current_lowest_price DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    alert_triggered BOOLEAN DEFAULT FALSE,
    triggered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_alerts (user_id, is_active)
);

-- Flight Cache Table
CREATE TABLE IF NOT EXISTS flight_cache (
    id BIGSERIAL PRIMARY KEY,
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    data JSONB NOT NULL,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(from_location, to_location, departure_date)
);

-- Train Cache Table
CREATE TABLE IF NOT EXISTS train_cache (
    id BIGSERIAL PRIMARY KEY,
    from_location VARCHAR(255) NOT NULL,
    to_location VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    data JSONB NOT NULL,
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(from_location, to_location, departure_date)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_search_history_user ON search_history(user_id);
CREATE INDEX IF NOT EXISTS idx_search_history_type ON search_history(search_type);
CREATE INDEX IF NOT EXISTS idx_price_alerts_user ON price_alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_price_alerts_active ON price_alerts(is_active);
CREATE INDEX IF NOT EXISTS idx_flight_cache_expiry ON flight_cache(expires_at);
CREATE INDEX IF NOT EXISTS idx_train_cache_expiry ON train_cache(expires_at);
