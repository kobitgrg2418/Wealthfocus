@echo off
echo ========================================
echo   WealthFocus - Clean Build and Run
echo ========================================
echo.

REM Check if we're in the right directory
if exist pom.xml (
    echo Found pom.xml in current directory
) else if exist java-version\pom.xml (
    echo Changing to java-version directory...
    cd java-version
) else (
    echo ERROR: Cannot find pom.xml!
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
echo   Starting Tomcat 7 (Embedded)
echo ========================================
echo.
echo IMPORTANT: This will use the embedded Tomcat 7
echo NOT your installed Tomcat 10
echo.
echo Once started, open your browser to:
echo   http://localhost:8080/
echo.
echo Press Ctrl+C to stop the server
echo.

mvn tomcat7:run

pause
