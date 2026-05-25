@echo off
REM Maven Build Helper Script for Travel Planner Backend
REM This script helps resolve Maven "not recognized" issues on Windows

echo.
echo ========================================
echo Travel Planner - Maven Build Helper
echo ========================================
echo.

REM Check if Maven is in PATH
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven found in PATH
    echo Running: mvn clean package
    mvn clean package
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo [SUCCESS] Build completed successfully!
        echo.
        echo To run the application:
        echo   mvn spring-boot:run
        echo.
        echo Then access Swagger UI at:
        echo   http://localhost:8080/swagger-ui.html
        echo.
    ) else (
        echo [ERROR] Build failed
    )
) else (
    echo [ERROR] Maven not found in PATH
    echo.
    echo Please install Maven first:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to: C:\Tools\apache-maven-3.9.x
    echo 3. Set MAVEN_HOME environment variable
    echo 4. Add %%MAVEN_HOME%%\bin to PATH
    echo.
    echo For detailed instructions, see: MAVEN_SETUP.md
    echo.
    
    REM Try to find Maven in common locations
    echo Searching for Maven in common locations...
    
    if exist "C:\Program Files\Maven\bin\mvn.cmd" (
        echo.
        echo [FOUND] Maven found at: C:\Program Files\Maven\bin
        echo Running with full path...
        "C:\Program Files\Maven\bin\mvn.cmd" clean package
    ) else if exist "C:\Tools\apache-maven-3.9.x\bin\mvn.cmd" (
        echo.
        echo [FOUND] Maven found at: C:\Tools\apache-maven-3.9.x\bin
        echo Running with full path...
        "C:\Tools\apache-maven-3.9.x\bin\mvn.cmd" clean package
    ) else if exist "%PROGRAMFILES%\Maven\bin\mvn.cmd" (
        echo.
        echo [FOUND] Maven found at: %PROGRAMFILES%\Maven\bin
        echo Running with full path...
        "%PROGRAMFILES%\Maven\bin\mvn.cmd" clean package
    ) else (
        echo.
        echo [NOT FOUND] Maven not found in common locations
        echo.
        echo Please follow MAVEN_SETUP.md to install Maven
    )
)

echo.
pause
