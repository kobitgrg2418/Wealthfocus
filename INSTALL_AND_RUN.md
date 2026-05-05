# WealthFocus Java/JSP - Complete Installation & Run Guide

## Current Status
✅ Java 23 is installed  
❌ Maven is NOT installed  
❌ MySQL needs to be checked/installed  

---

## Step 1: Install Maven

### Option A: Download Maven Manually (Recommended)

1. **Download Maven**
   - Go to: https://maven.apache.org/download.cgi
   - Download: `apache-maven-3.9.6-bin.zip` (Binary zip archive)

2. **Extract Maven**
   - Extract to: `C:\Program Files\Apache\maven`
   - Or any location you prefer (e.g., `C:\maven`)

3. **Set Environment Variables**
   
   **Add MAVEN_HOME:**
   - Press `Win + X` → System → Advanced system settings
   - Click "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\maven` (or your path)
   - Click OK

   **Add to PATH:**
   - In "System variables", find and select `Path`
   - Click "Edit"
   - Click "New"
   - Add: `%MAVEN_HOME%\bin`
   - Click OK on all dialogs

4. **Verify Installation**
   - Open a NEW PowerShell/Command Prompt window
   - Run: `mvn -v`
   - You should see Maven version info

### Option B: Using Chocolatey (If you have it)

```powershell
choco install maven
```

### Option C: Using Scoop (If you have it)

```powershell
scoop install maven
```

---

## Step 2: Install/Setup MySQL

### Check if MySQL is already installed:

```powershell
mysql --version
```

If not installed, choose one option:

### Option A: XAMPP (Easiest for Windows)

1. Download XAMPP: https://www.apachefriends.org/download.html
2. Install XAMPP
3. Open XAMPP Control Panel
4. Start "MySQL" module
5. Click "Shell" button
6. Run: `mysql -u root -e "CREATE DATABASE wealthfocus;"`

### Option B: MySQL Installer

1. Download: https://dev.mysql.com/downloads/installer/
2. Install MySQL Server
3. Set root password (or leave empty)
4. Create database:
   ```bash
   mysql -u root -p
   CREATE DATABASE wealthfocus;
   EXIT;
   ```

### Option C: Docker (If you have Docker Desktop)

```bash
docker run --name wealthfocus-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=wealthfocus -p 3306:3306 -d mysql:8
```

---

## Step 3: Configure Database Connection

1. **Edit the database properties file:**
   
   File: `src/main/resources/db.properties`

   ```properties
   db.host=localhost
   db.port=3306
   db.user=root
   db.password=
   db.name=wealthfocus
   ```

   **Important:** If you set a MySQL password, update `db.password=your_password_here`

---

## Step 4: Create Database Tables

### Option A: If you have the Node.js version migrations

```bash
cd java-version
mysql -u root wealthfocus < ../apps/api/src/db/migrations/001_create_users.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/002_create_categories.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/003_create_incomes.sql
mysql -u root wealthfocus < ../apps/api/src/db/migrations/004_create_expenses.sql
```

### Option B: Manual SQL (if migrations don't exist)

Connect to MySQL and run:

```sql
USE wealthfocus;

-- Users table
CREATE TABLE users (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email)
);

-- Categories table
CREATE TABLE categories (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  is_default BOOLEAN DEFAULT FALSE,
  user_id VARCHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  UNIQUE KEY unique_user_category (user_id, name)
);

-- Incomes table
CREATE TABLE incomes (
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
);

-- Expenses table
CREATE TABLE expenses (
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
);
```

---

## Step 5: Run the Application

### Navigate to java-version directory:

```bash
cd java-version
```

### Run with Maven:

```bash
mvn tomcat7:run
```

### Access the application:

Open your browser and go to: **http://localhost:8080/**

---

## 🎯 Quick Commands Summary

```bash
# After Maven and MySQL are installed:

# 1. Navigate to project
cd java-version

# 2. Run the app
mvn tomcat7:run

# 3. Open browser
# http://localhost:8080/
```

---

## Troubleshooting

### Maven not recognized after installation
- Close and reopen PowerShell/Command Prompt
- Verify PATH includes `%MAVEN_HOME%\bin`
- Run: `echo $env:PATH` to check

### Port 8080 already in use
```bash
mvn tomcat7:run -Dmaven.tomcat.port=8090
```
Then access: http://localhost:8090/

### MySQL connection error
- Ensure MySQL is running (XAMPP Control Panel or Windows Services)
- Check `src/main/resources/db.properties` has correct credentials
- Test connection: `mysql -u root -p`

### Database doesn't exist
```bash
mysql -u root -p -e "CREATE DATABASE wealthfocus;"
```

### Tables don't exist
- Run the SQL scripts from Step 4
- Or let the app auto-create the default user on first run

---

## Alternative: Build WAR file for deployment

```bash
cd java-version
mvn clean package
```

The WAR file will be in: `target/wealthfocus.war`

Deploy to any Tomcat server by copying to `webapps/` folder.

---

## Features

- 💰 Income & Expense tracking in Nepali Rupees (रू)
- 📊 Real-time net savings calculation
- 📈 Category breakdown with Chart.js visualizations
- 🤖 Investment recommendations (mocked for Nepal market)
- 🎨 Dark theme UI matching the React version
- 📱 Responsive design

---

## Need Help?

1. Check that Java is installed: `java -version`
2. Check that Maven is installed: `mvn -v`
3. Check that MySQL is running
4. Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
5. Check the logs in the terminal where `mvn tomcat7:run` is running
