# Building Native Executable with GraalVM

This guide explains how to build a standalone Windows executable (.exe) for the Bus Reservation Client using GraalVM Native Image.

## Prerequisites

### 1. Install GraalVM

Download and install GraalVM for Java 17:

**Option A: Via SDKMAN (Recommended for easy management)**
```powershell
# Install SDKMAN
# Follow instructions at https://sdkman.io/install

# Install GraalVM
sdk install java 17.0.9-graal

# Set as default
sdk use java 17.0.9-graal
```

**Option B: Manual Installation**
1. Download GraalVM from: https://www.graalvm.org/downloads/
2. Choose: **GraalVM Community Edition for Java 17** (Windows)
3. Extract to: `C:\Program Files\GraalVM\graalvm-ce-java17-XX.X.X`
4. Set environment variables:
   ```powershell
   # Set JAVA_HOME
   $env:JAVA_HOME = "C:\Program Files\GraalVM\graalvm-ce-java17-XX.X.X"
   
   # Add to PATH
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

### 2. Install Native Image Component

```powershell
# Install native-image tool
gu install native-image
```

### 3. Install Visual Studio Build Tools (Windows)

GraalVM Native Image requires C++ compiler on Windows:

1. Download **Visual Studio Build Tools**: https://visualstudio.microsoft.com/downloads/
2. Install **"Desktop development with C++"** workload
3. Or install full Visual Studio Community Edition

### 4. Verify Installation

```powershell
# Check Java version
java -version
# Should show: GraalVM CE 17.x.x

# Check native-image
native-image --version
# Should show: GraalVM Version XX.X.X
```

## Building the Native Executable

### Step 1: Clean and Package

```powershell
# Navigate to client directory
cd client

# Clean previous builds
mvn clean
```

### Step 2: Build Native Executable

```powershell
# Build with native profile
mvn package -Pnative

# This will:
# 1. Compile Java code
# 2. Run static analysis
# 3. Generate native executable: target/bus-reservation-client.exe
```

**Build time:** First build takes 2-5 minutes depending on your machine.

### Step 3: Test the Executable

```powershell
# Run the native executable
.\target\bus-reservation-client.exe

# With custom API URL
.\target\bus-reservation-client.exe http://localhost:8080/bus-reservation

# Enable debug mode
.\target\bus-reservation-client.exe -Ddebug=true
```

## Output

After successful build, you'll find:

- **Executable**: `target/bus-reservation-client.exe` (5-20 MB)
- **Build reports**: `target/native-image/`

## Distribution

The `.exe` file is **completely standalone**:
- ✅ No Java installation required
- ✅ No additional dependencies needed
- ✅ Can run on any Windows 10/11 machine
- ✅ Fast startup (milliseconds)
- ✅ Lower memory footprint

Simply copy `bus-reservation-client.exe` to any Windows machine and run it.

## Troubleshooting

### Error: "native-image not found"
```powershell
# Verify GraalVM is active
java -version

# Reinstall native-image
gu install native-image
```

### Error: "link.exe not found" or "cl.exe not found"
- Install Visual Studio Build Tools with C++ workload
- Make sure to run build from "x64 Native Tools Command Prompt for VS"

### Build fails with OutOfMemoryError
```powershell
# Increase memory
$env:MAVEN_OPTS = "-Xmx4g"
mvn package -Pnative
```

### Application crashes at runtime
- Check reflection configuration in `src/main/resources/META-INF/native-image/`
- Enable debug: `.\target\bus-reservation-client.exe -Ddebug=true`

## Advanced Options

### Custom Build Arguments

Edit `pom.xml` under `<buildArgs>`:

```xml
<buildArg>--no-fallback</buildArg>                    <!-- No JVM fallback -->
<buildArg>--enable-url-protocols=http,https</buildArg> <!-- Enable HTTP -->
<buildArg>-H:+ReportExceptionStackTraces</buildArg>    <!-- Better errors -->
<buildArg>-H:+PrintClassInitialization</buildArg>      <!-- Debug init -->
<buildArg>-O3</buildArg>                               <!-- Max optimization -->
```

### Reduce Executable Size

```xml
<buildArg>-Ob</buildArg>  <!-- Optimize for size -->
<buildArg>--gc=serial</buildArg>  <!-- Smaller GC -->
```

### Testing Before Native Build

Test with Java first to ensure everything works:

```powershell
# Build regular JAR
mvn package

# Run JAR
java -jar target/bus-reservation-client.jar
```

## Performance Comparison

| Metric | Java JAR | Native Executable |
|--------|----------|-------------------|
| Startup Time | ~1-2 seconds | ~50-100ms |
| Memory (idle) | ~50-100 MB | ~10-20 MB |
| Size | ~10 MB (+ JRE 150MB) | ~10-20 MB (standalone) |
| First Request | Slower (JIT warmup) | Fast immediately |

## CI/CD Integration

GitHub Actions example:

```yaml
- name: Setup GraalVM
  uses: graalvm/setup-graalvm@v1
  with:
    java-version: '17'
    distribution: 'graalvm'
    
- name: Build Native Image
  run: mvn package -Pnative
  working-directory: client
  
- name: Upload Executable
  uses: actions/upload-artifact@v3
  with:
    name: bus-reservation-client-windows
    path: client/target/bus-reservation-client.exe
```

## Additional Resources

- GraalVM Documentation: https://www.graalvm.org/latest/docs/
- Native Image Guide: https://www.graalvm.org/latest/reference-manual/native-image/
- Maven Plugin: https://graalvm.github.io/native-build-tools/latest/maven-plugin.html
