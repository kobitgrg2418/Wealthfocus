-- WealthFocus Database Setup - Complete Script
-- Run this file to create all tables and seed default data
--   mysql -u root -p < database/setup-database.sql

CREATE DATABASE IF NOT EXISTS wealthfocus;
USE wealthfocus;

-- ============================================
-- 1. users (with authentication support)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NULL,
  phone VARCHAR(20) NULL,
  address VARCHAR(500) NULL,
  photo_path VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. categories
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

-- Seed shared default categories (user_id = NULL → visible to every user).
-- CategoryDAO.findAvailable returns rows where user_id IS NULL OR user_id = ?
INSERT INTO categories (id, name, is_default, user_id) VALUES
  (UUID(), 'Groceries',      TRUE, NULL),
  (UUID(), 'Utilities',      TRUE, NULL),
  (UUID(), 'Entertainment',  TRUE, NULL),
  (UUID(), 'Transportation', TRUE, NULL),
  (UUID(), 'Healthcare',     TRUE, NULL),
  (UUID(), 'Housing',        TRUE, NULL)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 3. incomes
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
-- 4. expenses
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
