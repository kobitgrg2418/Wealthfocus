-- WealthFocus Database Setup - Complete Script
-- Run this file to create all tables and seed default data

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS wealthfocus;
USE wealthfocus;

-- ============================================
-- 1. Create users table
-- ============================================
CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default user for Java version
INSERT INTO users (id, email, name) 
VALUES ('default-user-id', 'jhon@wealthfocus.com', 'Jhon')
ON DUPLICATE KEY UPDATE name=name;

-- ============================================
-- 2. Create categories table
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  is_default BOOLEAN DEFAULT FALSE,
  user_id VARCHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  UNIQUE KEY unique_user_category (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default categories
INSERT INTO categories (id, name, is_default, user_id) VALUES
  (UUID(), 'Groceries', TRUE, 'default-user-id'),
  (UUID(), 'Utilities', TRUE, 'default-user-id'),
  (UUID(), 'Entertainment', TRUE, 'default-user-id'),
  (UUID(), 'Transportation', TRUE, 'default-user-id'),
  (UUID(), 'Healthcare', TRUE, 'default-user-id'),
  (UUID(), 'Housing', TRUE, 'default-user-id')
ON DUPLICATE KEY UPDATE name=name;

-- ============================================
-- 3. Create incomes table
-- ============================================
CREATE TABLE IF NOT EXISTS incomes (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
  source VARCHAR(255) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_date (user_id, date),
  INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. Create expenses table
-- ============================================
CREATE TABLE IF NOT EXISTS expenses (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
  description VARCHAR(255) NOT NULL,
  category_id VARCHAR(36) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
  INDEX idx_user_date (user_id, date),
  INDEX idx_category (category_id),
  INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Verification
-- ============================================
SELECT 'Database setup complete!' AS status;
SHOW TABLES;
