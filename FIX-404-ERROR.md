# Fix 404 Error - WealthFocus

## Problem
You're seeing:
```
HTTP Status 404 – Not Found
The requested resource [/wealthfocus/] is not available
Apache Tomcat/10.1.54
```

## Root Cause
The error shows **Tomcat 10.1.54**, but the project is configured for **Tomcat 7**. This happens when:
1. You have Tomcat 10 installed separately and it's running
2. The Maven Tomcat 7 plugin isn't being used correctly

## Solutions

### Solution 1: Use Clean Build Script (Recommended)

1. **Stop any running Tomcat servers**
   - Close any browser tabs with localhost:8080
   - Press Ctrl+C in any terminals running Tomcat
   - Check Windows Services and stop "Apache Tomcat" if running

2. **Run the clean build:**
   ```bash
   CLEAN-AND-RUN.bat
   ```

This will:
- Clean previous builds
- Compile the project
- Start embedded Tomcat 7 (not your installed Tomcat 10)

### Solution 2: Manual Maven Commands

```bash
cd java-version
mvn clean
mvn compile
mvn tomcat7:run
```

### Solution 3: Check for Running Tomcat

**Find and stop any Tomcat process:**

```powershell
# Find processes using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

Then run:
```bash
mvn tomcat7:run
```

### Solution 4: Use Different Port

If port 8080 is occupied:

```bash
mvn tomcat7:run -Dmaven.tomcat.port=8090
```

Then open: http://localhost:8090/

## Verification

### Correct Startup Should Show:

```
[INFO] Running war on http://localhost:8080/
[INFO] Using existing Tomcat server configuration at ...
[INFO] Starting tomcat7:run
```

### You Should See:
- **Tomcat 7** (not Tomcat 10)
- Path: `/` (not `/wealthfocus/`)
- Port: `8080`

## Common Issues

### Issue 1: "Address already in use"
**Solution:** Another process is using port 8080
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Then run again
mvn tomcat7:run
```

### Issue 2: "BUILD FAILURE - compilation error"
**Solution:** Clean and rebuild
```bash
mvn clean compile
mvn tomcat7:run
```

### Issue 3: Still seeing Tomcat 10
**Solution:** You're accessing the wrong Tomcat
- Make sure you're using `mvn tomcat7:run` (embedded Tomcat 7)
- NOT deploying to external Tomcat 10
- Stop any external Tomcat services

### Issue 4: "ClassNotFoundException" or "NoClassDefFoundError"
**Solution:** Dependencies not downloaded
```bash
mvn clean install
mvn tomcat7:run
```

## Understanding the Setup

### What You Have:
- **External Tomcat 10** (installed separately) - DON'T USE THIS
- **Maven Tomcat 7 Plugin** (embedded) - USE THIS

### What to Use:
✅ **Use:** `mvn tomcat7:run` (embedded Tomcat 7)  
❌ **Don't:** Deploy WAR to external Tomcat 10

### Why Tomcat 7?
The project uses `javax.servlet` API (old Java EE), which works with:
- ✅ Tomcat 7, 8, 9
- ❌ Tomcat 10+ (requires `jakarta.servlet`)

## Quick Fix Checklist

- [ ] Stop all Tomcat processes
- [ ] Close browser tabs with localhost:8080
- [ ] Run `CLEAN-AND-RUN.bat`
- [ ] Wait for "Running war on http://localhost:8080/"
- [ ] Open NEW browser tab to http://localhost:8080/
- [ ] Should see WealthFocus dashboard

## Still Not Working?

### Check Maven is using Tomcat 7:
```bash
mvn tomcat7:help
```

Should show Tomcat 7 plugin info.

### Check Java version:
```bash
java -version
```

Should show Java 11 or higher.

### Check project compiles:
```bash
mvn clean compile
```

Should show "BUILD SUCCESS".

### Check database connection:
```bash
mysql -u root -p -e "USE wealthfocus; SHOW TABLES;"
```

Should show 4 tables.

## Alternative: Build WAR for External Tomcat

If you really want to use external Tomcat 10, you need to:

1. **Upgrade to Jakarta EE** (major code changes)
2. **Or downgrade to Tomcat 9**

**Easier:** Just use `mvn tomcat7:run` as intended!

## Success!

Once working, you should see:
- URL: http://localhost:8080/
- WealthFocus dashboard with dark theme
- No 404 errors
- Able to add income/expenses

---

**TL;DR:** Run `CLEAN-AND-RUN.bat` and make sure no other Tomcat is running!
