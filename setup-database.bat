@echo off
echo ========================================
echo   WealthFocus Database Setup
echo ========================================
echo.

echo This will create the database and all tables.
echo.

REM Check if MySQL is accessible
mysql --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: MySQL is not installed or not in PATH!
    echo.
    echo Please install MySQL first:
    echo   - XAMPP: https://www.apachefriends.org/download.html
    echo   - MySQL: https://dev.mysql.com/downloads/installer/
    echo.
    pause
    exit /b 1
)

echo MySQL found!
echo.

REM Prompt for password
set /p MYSQL_PASSWORD="Enter MySQL root password (press Enter if no password): "

echo.
echo Running database setup...
echo.

if "%MYSQL_PASSWORD%"=="" (
    mysql -u root < database\setup-database.sql
) else (
    mysql -u root -p%MYSQL_PASSWORD% < database\setup-database.sql
)

if errorlevel 1 (
    echo.
    echo ERROR: Database setup failed!
    echo Please check your MySQL credentials and try again.
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Database Setup Complete!
echo ========================================
echo.
echo Created:
echo   - Database: wealthfocus
echo   - Tables: users, categories, incomes, expenses
echo   - Default user: Jhon
echo   - 6 default categories
echo.
echo You can now run the application with RUN.bat
echo.
pause
