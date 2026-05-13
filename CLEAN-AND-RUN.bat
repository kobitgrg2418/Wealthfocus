@echo off
echo ========================================
echo   WealthFocus - Clean Build and Run
echo ========================================
echo.

if not exist pom.xml (
    echo ERROR: Cannot find pom.xml in current directory!
    pause
    exit /b 1
)

echo.
echo Cleaning previous build...
call mvn clean

echo.
echo Compiling project...
call mvn compile

echo.
echo ========================================
echo   Starting embedded Tomcat 10 (Cargo)
echo ========================================
echo.
echo Once started, open your browser to:
echo   http://localhost:8080/
echo.
echo Press Ctrl+C to stop the server
echo.

mvn cargo:run

pause
