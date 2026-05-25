# Maven Setup & Build Instructions for Windows

## Problem: "mvn is not recognized"

Maven is not in your system PATH. Here are three solutions:

---

## Solution 1: Use Maven Wrapper (Easiest - No Installation Required)

If Maven Wrapper is available in your project:

```powershell
cd "C:\Users\aarti\rt\programming\travel plan"

# On Windows, use mvnw.cmd instead of mvn
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

This doesn't require Maven to be installed globally!

---

## Solution 2: Install Maven Properly

### Step 1: Download Maven
1. Go to https://maven.apache.org/download.cgi
2. Download "Binary zip archive" (maven-3.9.x-bin.zip or latest)
3. Extract to: `C:\Tools\apache-maven-3.9.x`

### Step 2: Add Maven to PATH (Windows)
1. Right-click "This PC" or "My Computer" → Properties
2. Click "Advanced system settings"
3. Click "Environment Variables" button
4. Under "System variables", click "New"
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Tools\apache-maven-3.9.x`
5. Find "Path" in System variables and click "Edit"
6. Click "New" and add: `%MAVEN_HOME%\bin`
7. Click OK on all dialogs

### Step 3: Verify Installation
```powershell
# Close PowerShell and open a new one, then run:
mvn --version
```

---

## Solution 3: Use Full Maven Path (Quick Fix)

If you have Maven installed somewhere, use the full path:

```powershell
"C:\Program Files\Maven\apache-maven-3.9.x\bin\mvn.cmd" clean package
```

Replace the path with your actual Maven installation directory.

---

## Solution 4: Use Java Compiler Directly (Not Recommended)

```powershell
cd "C:\Users\aarti\rt\programming\travel plan"
javac -version
java -version
# Then use gradlew if available
```

---

## Recommended: Setup Steps for Windows PowerShell

### Quick Setup (Assuming Maven already installed):

```powershell
# 1. Check if mvn wrapper exists
cd "C:\Users\aarti\rt\programming\travel plan"
ls -Name | findstr mvnw

# 2. If mvnw.cmd exists, use it:
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run

# 3. If mvn is installed globally:
mvn clean package
mvn spring-boot:run

# 4. If Maven installed at custom location:
$env:MAVEN_HOME = "C:\Tools\apache-maven-3.9.x"
$env:PATH = "$env:MAVEN_HOME\bin;$env:PATH"
mvn clean package
```

---

## For This Project: What to Do Now

### Option A: Install Maven (Permanent Solution)
```powershell
# Follow Solution 2 above, then run:
cd "C:\Users\aarti\rt\programming\travel plan"
mvn clean package
```

### Option B: Use Maven Wrapper (if available)
```powershell
cd "C:\Users\aarti\rt\programming\travel plan"
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

### Option C: Use IDE
- Open project in IntelliJ IDEA or VS Code with Maven extension
- Run: Maven → clean
- Run: Maven → package
- Or use IDE's "Run" button

---

## Build Commands After Setup

```powershell
# Full build with tests
mvn clean package

# Build without running tests (faster)
mvn clean package -DskipTests

# Run tests only
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Start application
mvn spring-boot:run

# Generate API documentation
mvn javadoc:javadoc

# Build and deploy
mvn clean deploy
```

---

## After Building Successfully

Your application will be available at:

```
http://localhost:8080
```

### Important URLs:

- **API Documentation (Swagger UI)**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

### Test Login:
```bash
POST http://localhost:8080/api/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "TestPass123!",
  "firstName": "Test",
  "lastName": "User"
}
```

---

## Troubleshooting

### Error: "Cannot find symbol" or "class not found"
```powershell
# Clear Maven cache
mvn clean
# Then rebuild
mvn package
```

### Error: Database connection failed
- Make sure PostgreSQL is running
- Check database credentials in application.yml
- Update: spring.datasource.url, username, password

### Error: Java version mismatch
```powershell
# Check Java version (needs 21+)
java -version

# If Java 21 not installed, set JAVA_HOME:
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.x.x"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
```

---

## Next Steps After Build

1. ✅ **Build succeeds**
   - Run: `mvn clean package`
   - JAR created: `target/travel-planner-backend-1.0.0.jar`

2. ✅ **Start application**
   - Run: `mvn spring-boot:run`
   - Or: `java -jar target/travel-planner-backend-1.0.0.jar`

3. ✅ **Test API with Swagger**
   - Open: http://localhost:8080/swagger-ui.html
   - Try register and login endpoints
   - Get JWT token
   - Test flight search with token

4. ✅ **View API docs**
   - JSON: http://localhost:8080/v3/api-docs

---

## Questions?

If Maven still won't work:
1. Check your Java installation: `java -version` (should be 21+)
2. Verify Python 3 is installed (for some build tools)
3. Try IDE with built-in Maven support (IntelliJ, VS Code)
4. Or use Docker: `docker build -t travel-planner .`
