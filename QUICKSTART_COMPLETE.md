# 🚀 WealthFocus - Complete Quick Start Guide

## ✅ What You Have
- Java 23 installed

## ❌ What You Need
1. Maven
2. MySQL
3. Database setup

---

## 📦 Step 1: Install Maven

### Download & Install
1. Go to: https://maven.apache.org/download.cgi
2. Download: **apache-maven-3.9.6-bin.zip**
3. Extract to: `C:\Program Files\Apache\maven`

### Add to PATH
1. Press `Win + X` → **System**
2. Click **Advanced system settings**
3. Click **Environment Variables**
4. Under **System variables**, click **New**:
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\maven`
5. Find **Path** in System variables, click **Edit**
6. Click **New**, add: `%MAVEN_HOME%\bin`
7. Click **OK** on all dialogs

### Verify
Open **NEW** PowerShell and run:
```bash
mvn -v
```

---

## 🗄️ Step 2: Install MySQL

### Option A: XAMPP (Recommended - Easiest)
1. Download: https://www.apachefriends.org/download.html
2. Install XAMPP
3. Open **XAMPP Control Panel**
4. Click **Start** next to MySQL
5. Done! ✓

### Option B: MySQL Installer
1. Download: https://dev.mysql.com/downloads/installer/
2. Install MySQL Server
3. Remember your root password (or leave empty)
4. Start MySQL service

---

## 🎯 Step 3: Setup Database

### Easiest Way - Double Click
Simply double-click: **`setup-database.bat`**

This will:
- Create the `wealthfocus` database
- Create all tables
- Insert default user and categories

### Alternative - Manual Command
```bash
mysql -u root -p < database/setup-database.sql
```

---

## 🏃 Step 4: Run the Application

### Easiest Way - Double Click
Double-click: **`RUN.bat`**

### Alternative - Command Line
```bash
cd java-version
mvn tomcat7:run
```

---

## 🌐 Step 5: Open in Browser

Go to: **http://localhost:8080/**

---

## 📋 Complete Checklist

- [ ] Install Maven
- [ ] Add Maven to PATH
- [ ] Restart PowerShell
- [ ] Verify: `mvn -v`
- [ ] Install MySQL (XAMPP or MySQL Installer)
- [ ] Start MySQL service
- [ ] Run `setup-database.bat`
- [ ] Run `RUN.bat`
- [ ] Open http://localhost:8080/

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `setup-database.bat` | Creates database and tables |
| `RUN.bat` | Starts the application |
| `database/setup-database.sql` | Complete database setup script |
| `database/*.sql` | Individual table creation scripts |
| `src/main/resources/db.properties` | Database configuration |

---

## ⚙️ Configuration

If you set a MySQL password, edit: `src/main/resources/db.properties`

```properties
db.host=localhost
db.port=3306
db.user=root
db.password=your_password_here
db.name=wealthfocus
```

---

## 🔧 Troubleshooting

### Maven not found after installation
- **Close and reopen PowerShell** (important!)
- Verify PATH: `echo $env:PATH`
- Should contain: `%MAVEN_HOME%\bin`

### MySQL not found
- If using XAMPP: Start MySQL from XAMPP Control Panel
- If using MySQL Installer: Start MySQL80 service in Windows Services

### Database setup fails
- Ensure MySQL is running
- Check if you need a password: `mysql -u root -p`
- Try running individual scripts from `database/` folder

### Port 8080 already in use
```bash
mvn tomcat7:run -Dmaven.tomcat.port=8090
```
Then open: http://localhost:8090/

---

## 🎉 Success!

Once running, you can:
- ✅ Add income entries (in Nepali Rupees रू)
- ✅ Add expenses with categories
- ✅ View real-time net savings
- ✅ See category breakdown charts
- ✅ Get investment recommendations

---

## 📚 More Help

- **Database Setup:** See `database/README.md`
- **Full Installation:** See `INSTALL_AND_RUN.md`
- **Running Guide:** See `HOW_TO_RUN.md`
- **Project Info:** See `README.md`

---

## 🆘 Still Having Issues?

1. Check Java: `java -version` (should show Java 11+)
2. Check Maven: `mvn -v` (should show Maven 3.6+)
3. Check MySQL: `mysql --version` (should show MySQL 5.7+)
4. Test MySQL connection: `mysql -u root -p`
5. Verify database: `mysql -u root -p -e "SHOW DATABASES;"`

---

**That's it! You're ready to manage your finances with WealthFocus! 💰**
