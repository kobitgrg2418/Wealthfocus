# 🚀 How to Run WealthFocus Java/JSP

## Current System Status
- ✅ **Java 23** is installed
- ❌ **Maven** needs to be installed
- ❌ **MySQL** needs to be checked/installed

---

## 🎯 Three Ways to Run

### Method 1: Double-Click (Easiest) ⭐

**After installing Maven and MySQL:**

1. Double-click **`RUN.bat`**
2. Wait for the server to start
3. Open browser to http://localhost:8080/

### Method 2: Automated Setup Script

**Run the PowerShell setup script:**

```powershell
powershell -ExecutionPolicy Bypass -File setup-and-run.ps1
```

This script will:
- ✓ Check if Java is installed
- ✓ Check if Maven is installed
- ✓ Check if MySQL is installed and running
- ✓ Create database if it doesn't exist
- ✓ Create default configuration files
- ✓ Start the application

### Method 3: Manual Maven Command

```bash
mvn cargo:run
```

Then open: http://localhost:8080/

> The project uses `jakarta.servlet 6.0`, so it must run on **Tomcat 10+**.
> The `cargo-maven3-plugin` launches an embedded Tomcat 10 for local dev.
> To build a deployable WAR for an external Tomcat 10 server, run `mvn package`
> and drop `target/wealthfocus.war` into Tomcat's `webapps/` directory.

---

## 📋 Prerequisites Installation

### 1. Install Maven (Required)

**Quick Install Options:**

**Option A: Download Manually**
1. Download: https://maven.apache.org/download.cgi
2. Extract to: `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Win + X → System → Advanced → Environment Variables
   - Add `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - Add `%MAVEN_HOME%\bin` to PATH
4. Restart PowerShell
5. Verify: `mvn -v`

**Option B: Chocolatey**
```powershell
choco install maven
```

**Option C: Scoop**
```powershell
scoop install maven
```

### 2. Install MySQL (Required)

**Quick Install Options:**

**Option A: XAMPP (Easiest)**
1. Download: https://www.apachefriends.org/download.html
2. Install and start MySQL from XAMPP Control Panel
3. Create database:
   ```bash
   mysql -u root -e "CREATE DATABASE wealthfocus;"
   ```

**Option B: MySQL Installer**
1. Download: https://dev.mysql.com/downloads/installer/
2. Install MySQL Server
3. Create database:
   ```bash
   mysql -u root -p
   CREATE DATABASE wealthfocus;
   EXIT;
   ```

**Option C: Docker**
```bash
docker run --name wealthfocus-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=wealthfocus -p 3306:3306 -d mysql:8
```

---

## ⚙️ Configuration

### Database Configuration

Edit: `src/main/resources/db.properties`

```properties
db.host=localhost
db.port=3306
db.user=root
db.password=          # Add your MySQL password here if you set one
db.name=wealthfocus
```

---

## 🗄️ Database Setup

### Create Tables (Choose One)

**Option A: Auto-create on first run**
- The app will auto-create a default user on first request
- Tables should already exist from Node.js version

**Option B: Run SQL manually**

```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE wealthfocus;

-- Create tables (if they don't exist)
-- See INSTALL_AND_RUN.md for full SQL scripts
```

---

## 🎮 Running the Application

### Start the Server

**Using the batch file:**
```bash
RUN.bat
```

**Using PowerShell script:**
```powershell
powershell -ExecutionPolicy Bypass -File setup-and-run.ps1
```

**Using Maven directly:**
```bash
cd java-version
mvn tomcat7:run
```

### Access the Application

Open your browser to: **http://localhost:8080/**

### Stop the Server

Press **Ctrl + C** in the terminal

---

## 🔧 Troubleshooting

### Maven not found
```
'mvn' is not recognized...
```
**Solution:** Install Maven and add to PATH (see Prerequisites above)

### MySQL connection error
```
Communications link failure
```
**Solution:** 
- Ensure MySQL is running (XAMPP Control Panel or Windows Services)
- Check credentials in `src/main/resources/db.properties`

### Port 8080 already in use
```
Address already in use
```
**Solution:** Run on different port:
```bash
mvn tomcat7:run -Dmaven.tomcat.port=8090
```
Then access: http://localhost:8090/

### Database doesn't exist
```
Unknown database 'wealthfocus'
```
**Solution:**
```bash
mysql -u root -p -e "CREATE DATABASE wealthfocus;"
```

### Access denied for user 'root'
```
Access denied for user 'root'@'localhost'
```
**Solution:** Update password in `src/main/resources/db.properties`

---

## 📊 Features

Once running, you can:

- ✅ Add income entries (in Nepali Rupees रू)
- ✅ Add expense entries with categories
- ✅ View real-time net savings calculation
- ✅ See category breakdown with Chart.js visualizations
- ✅ Get investment recommendations (mocked for Nepal market)
- ✅ Filter by time periods (This Month, Last Month, etc.)

---

## 🏗️ Build for Production

To create a deployable WAR file:

```bash
cd java-version
mvn clean package
```

Output: `target/wealthfocus.war`

Deploy to any Tomcat server by copying to `webapps/` folder.

---

## 📚 Additional Resources

- **Full Setup Guide:** [INSTALL_AND_RUN.md](./INSTALL_AND_RUN.md)
- **Project README:** [README.md](./README.md)
- **Maven Documentation:** https://maven.apache.org/guides/
- **Tomcat Documentation:** https://tomcat.apache.org/

---

## 🆘 Quick Help

**Check installations:**
```bash
java -version    # Should show Java 11+
mvn -v          # Should show Maven 3.6+
mysql --version # Should show MySQL 5.7+
```

**Verify MySQL is running:**
```bash
mysql -u root -p -e "SELECT 1;"
```

**Check if database exists:**
```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'wealthfocus';"
```

---

## 🎯 Summary: Fastest Way to Run

1. **Install Maven** (if not installed)
2. **Install MySQL** (if not installed)
3. **Create database:** `mysql -u root -p -e "CREATE DATABASE wealthfocus;"`
4. **Double-click:** `RUN.bat`
5. **Open browser:** http://localhost:8080/

That's it! 🎉
