# WealthFocus Database Setup

This folder contains all SQL scripts needed to set up the WealthFocus database.

## Quick Setup

### Option 1: Run the Batch File (Easiest)

Simply double-click: **`setup-database.bat`** (in the parent directory)

This will:
- Create the `wealthfocus` database
- Create all tables (users, categories, incomes, expenses)
- Insert default user and categories

### Option 2: Run Complete SQL Script

```bash
mysql -u root -p < database/setup-database.sql
```

### Option 3: Run Individual Scripts

```bash
mysql -u root -p wealthfocus < database/001_create_users.sql
mysql -u root -p wealthfocus < database/002_create_categories.sql
mysql -u root -p wealthfocus < database/003_create_incomes.sql
mysql -u root -p wealthfocus < database/004_create_expenses.sql
```

### Option 4: Copy-Paste in MySQL Workbench

1. Open MySQL Workbench
2. Connect to your local MySQL server
3. Open `database/setup-database.sql`
4. Execute the script

## Files

- **`setup-database.sql`** - Complete setup script (creates database + all tables)
- **`001_create_users.sql`** - Users table + default user
- **`002_create_categories.sql`** - Categories table + 6 default categories
- **`003_create_incomes.sql`** - Incomes table
- **`004_create_expenses.sql`** - Expenses table

## Default Data

### Default User
- **ID:** `default-user-id`
- **Email:** `jhon@wealthfocus.com`
- **Name:** `Jhon`

### Default Categories
1. Groceries
2. Utilities
3. Entertainment
4. Transportation
5. Healthcare
6. Housing

## Database Schema

```
wealthfocus
├── users
│   ├── id (VARCHAR 36, PK)
│   ├── email (VARCHAR 255, UNIQUE)
│   ├── name (VARCHAR 255)
│   ├── created_at (TIMESTAMP)
│   └── updated_at (TIMESTAMP)
│
├── categories
│   ├── id (VARCHAR 36, PK)
│   ├── name (VARCHAR 100)
│   ├── is_default (BOOLEAN)
│   ├── user_id (VARCHAR 36, FK → users.id)
│   └── created_at (TIMESTAMP)
│
├── incomes
│   ├── id (VARCHAR 36, PK)
│   ├── user_id (VARCHAR 36, FK → users.id)
│   ├── amount (DECIMAL 12,2)
│   ├── source (VARCHAR 255)
│   ├── date (DATE)
│   ├── created_at (TIMESTAMP)
│   └── updated_at (TIMESTAMP)
│
└── expenses
    ├── id (VARCHAR 36, PK)
    ├── user_id (VARCHAR 36, FK → users.id)
    ├── amount (DECIMAL 12,2)
    ├── description (VARCHAR 255)
    ├── category_id (VARCHAR 36, FK → categories.id)
    ├── date (DATE)
    ├── created_at (TIMESTAMP)
    └── updated_at (TIMESTAMP)
```

## Verification

After running the setup, verify with:

```sql
USE wealthfocus;
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM categories;
```

You should see:
- 4 tables created
- 1 user (Jhon)
- 6 categories

## Troubleshooting

### "Access denied for user 'root'"
- Check your MySQL root password
- Update the command with correct credentials

### "Database already exists"
- This is fine! The script uses `CREATE TABLE IF NOT EXISTS`
- It will skip existing tables and only create missing ones

### "Can't connect to MySQL server"
- Ensure MySQL is running
- XAMPP: Start MySQL from Control Panel
- Windows Service: Start MySQL80 service

### "Unknown database 'wealthfocus'"
- The complete script (`setup-database.sql`) creates the database
- Or manually create it: `CREATE DATABASE wealthfocus;`

## Next Steps

After database setup:
1. Configure `src/main/resources/db.properties` with your MySQL credentials
2. Run the application: `RUN.bat` or `mvn tomcat7:run`
3. Access: http://localhost:8080/
