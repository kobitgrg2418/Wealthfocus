# WealthFocus Java/JSP - Automated Setup and Run Script
# Run this script with: powershell -ExecutionPolicy Bypass -File setup-and-run.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  WealthFocus Java/JSP Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Function to check if a command exists
function Test-Command {
    param($Command)
    try {
        if (Get-Command $Command -ErrorAction Stop) {
            return $true
        }
    }
    catch {
        return $false
    }
}

# Check Java
Write-Host "Checking Java installation..." -ForegroundColor Yellow
if (Test-Command "java") {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    Write-Host "✓ Java is installed: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "✗ Java is NOT installed!" -ForegroundColor Red
    Write-Host "  Please install Java 11+ from: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Yellow
    exit 1
}

# Check Maven
Write-Host "Checking Maven installation..." -ForegroundColor Yellow
if (Test-Command "mvn") {
    $mavenVersion = mvn -v | Select-String "Apache Maven" | Select-Object -First 1
    Write-Host "✓ Maven is installed: $mavenVersion" -ForegroundColor Green
} else {
    Write-Host "✗ Maven is NOT installed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Maven using one of these methods:" -ForegroundColor Yellow
    Write-Host "  1. Download from: https://maven.apache.org/download.cgi" -ForegroundColor White
    Write-Host "  2. Or use Chocolatey: choco install maven" -ForegroundColor White
    Write-Host "  3. Or use Scoop: scoop install maven" -ForegroundColor White
    Write-Host ""
    Write-Host "After installation, restart PowerShell and run this script again." -ForegroundColor Yellow
    exit 1
}

# Check MySQL
Write-Host "Checking MySQL installation..." -ForegroundColor Yellow
if (Test-Command "mysql") {
    $mysqlVersion = mysql --version
    Write-Host "✓ MySQL is installed: $mysqlVersion" -ForegroundColor Green
    
    # Test MySQL connection
    Write-Host "Testing MySQL connection..." -ForegroundColor Yellow
    $mysqlTest = mysql -u root -e "SELECT 1;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ MySQL connection successful" -ForegroundColor Green
        
        # Check if database exists
        Write-Host "Checking if 'wealthfocus' database exists..." -ForegroundColor Yellow
        $dbCheck = mysql -u root -e "SHOW DATABASES LIKE 'wealthfocus';" 2>&1
        if ($dbCheck -match "wealthfocus") {
            Write-Host "✓ Database 'wealthfocus' exists" -ForegroundColor Green
        } else {
            Write-Host "! Database 'wealthfocus' does not exist. Creating..." -ForegroundColor Yellow
            mysql -u root -e "CREATE DATABASE wealthfocus;" 2>&1
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✓ Database 'wealthfocus' created successfully" -ForegroundColor Green
            } else {
                Write-Host "✗ Failed to create database" -ForegroundColor Red
                exit 1
            }
        }
    } else {
        Write-Host "✗ Cannot connect to MySQL" -ForegroundColor Red
        Write-Host "  Please ensure MySQL is running and check your credentials" -ForegroundColor Yellow
        Write-Host "  Edit src/main/resources/db.properties if needed" -ForegroundColor Yellow
    }
} else {
    Write-Host "✗ MySQL is NOT installed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install MySQL using one of these methods:" -ForegroundColor Yellow
    Write-Host "  1. XAMPP (easiest): https://www.apachefriends.org/download.html" -ForegroundColor White
    Write-Host "  2. MySQL Installer: https://dev.mysql.com/downloads/installer/" -ForegroundColor White
    Write-Host "  3. Docker: docker run --name wealthfocus-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=wealthfocus -p 3306:3306 -d mysql:8" -ForegroundColor White
    Write-Host ""
    Write-Host "After installation, restart PowerShell and run this script again." -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  All prerequisites are installed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the right directory
if (Test-Path "pom.xml") {
    Write-Host "✓ Found pom.xml in current directory" -ForegroundColor Green
} elseif (Test-Path "java-version/pom.xml") {
    Write-Host "! Changing to java-version directory..." -ForegroundColor Yellow
    Set-Location java-version
} else {
    Write-Host "✗ Cannot find pom.xml!" -ForegroundColor Red
    Write-Host "  Please run this script from the project root or java-version directory" -ForegroundColor Yellow
    exit 1
}

# Check if db.properties exists
if (Test-Path "src/main/resources/db.properties") {
    Write-Host "✓ Database configuration file exists" -ForegroundColor Green
} else {
    Write-Host "! Creating default db.properties..." -ForegroundColor Yellow
    $dbPropertiesDir = "src/main/resources"
    if (-not (Test-Path $dbPropertiesDir)) {
        New-Item -ItemType Directory -Path $dbPropertiesDir -Force | Out-Null
    }
    
    $dbPropertiesContent = @"
db.host=localhost
db.port=3306
db.user=root
db.password=
db.name=wealthfocus
"@
    Set-Content -Path "$dbPropertiesDir/db.properties" -Value $dbPropertiesContent
    Write-Host "✓ Created db.properties with default settings" -ForegroundColor Green
    Write-Host "  Edit src/main/resources/db.properties if you need to change MySQL credentials" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting WealthFocus Application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Running: mvn tomcat7:run" -ForegroundColor Yellow
Write-Host ""
Write-Host "Once started, open your browser to:" -ForegroundColor Green
Write-Host "  http://localhost:8080/" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Run Maven
mvn tomcat7:run
