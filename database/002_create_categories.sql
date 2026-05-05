-- Create categories table
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
