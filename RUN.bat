@echo off
echo ========================================
echo   WealthFocus Java/JSP Quick Run
echo ========================================
echo.

REM Check if Maven is installed
mvn -v >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed!
    echo.
    echo Please install Maven first:
    echo   Download: https://maven.apache.org/download.cgi
    echo   Or see: INSTALL_AND_RUN.md
    echo.
    pause
    exit /b 1
)

REM Check if MySQL is installed
mysql --version >nul 2>&1
if errorlevel 1 (
    echo WARNING: MySQL is not installed or not in PATH!
    echo.
    echo Please install MySQL first:
    echo   - XAMPP: https://www.apachefriends.org/download.html
    echo   - MySQL: https://dev.mysql.com/downloads/installer/
    echo.
    echo After installing MySQL, run: setup-database.bat
    echo.
    pause
    exit /b 1
)

REM Check if we're in the right directory
if exist pom.xml (
    echo Found pom.xml in current directory
) else if exist java-version\pom.xml (
    echo Changing to java-version directory...
    cd java-version
) else (
    echo ERROR: Cannot find pom.xml!
    echo Please run this script from the project root or java-version directory
    pause
    exit /b 1
)

echo.
echo Checking database setup...
echo.

REM Check if database exists (silent check)
mysql -u root -e "USE wealthfocus;" 2>nul
if errorlevel 1 (
    echo WARNING: Database 'wealthfocus' not found!
    echo.
    echo Please run the database setup first:
    echo   Double-click: setup-database.bat
    echo.
    set /p SETUP="Do you want to run database setup now? (Y/N): "
    if /i "%SETUP%"=="Y" (
        cd ..
        call setup-database.bat
        cd java-version
    ) else (
        echo.
        echo Please run setup-database.bat before starting the application.
        pause
        exit /b 1
    )
)

echo Database found!
echo.
echo Starting WealthFocus...
echo.
echo Once started, open your browser to:
echo   http://localhost:8080/
echo.
echo Press Ctrl+C to stop the server
echo.

mvn tomcat7:run

pause
