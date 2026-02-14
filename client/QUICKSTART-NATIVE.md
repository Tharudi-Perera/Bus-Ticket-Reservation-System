# Quick Start: Building Native Executable

## Prerequisites
1. Install **GraalVM for Java 17**: https://www.graalvm.org/downloads/
2. Install **native-image**: `gu install native-image`
3. Install **Visual Studio Build Tools** (C++ workload)

## Build Steps

### Option 1: Using Build Script (Easiest)
```powershell
cd client
.\build-native.bat
```

### Option 2: Manual Build
```powershell
cd client
mvn clean package -Pnative
```

## Result
- Native executable: `target\bus-reservation-client.exe`
- Size: ~10-20 MB
- No Java required to run!

## Run the Executable
```powershell
# Default API URL
.\target\bus-reservation-client.exe

# Custom API URL
.\target\bus-reservation-client.exe http://localhost:8080/bus-reservation
```

## Full Documentation
See [BUILD-NATIVE.md](BUILD-NATIVE.md) for complete instructions and troubleshooting.

## Quick Verification
```powershell
# 1. Verify GraalVM
java -version
# Should show: GraalVM CE 17.x.x

# 2. Verify native-image
native-image --version

# 3. Build
mvn package -Pnative

# 4. Run
.\target\bus-reservation-client.exe
```

## Benefits of Native Executable
✅ **Fast Startup**: 50-100ms vs 1-2 seconds  
✅ **Low Memory**: 10-20 MB vs 50-100 MB  
✅ **No JRE Required**: Standalone executable  
✅ **Easy Distribution**: Single .exe file  
