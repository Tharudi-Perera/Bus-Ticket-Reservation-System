@echo off
REM Build script for GraalVM Native Image on Windows

echo ========================================
echo Bus Reservation Client - Native Build
echo ========================================
echo.

REM Check if GraalVM is installed
java -version 2>&1 | findstr /C:"GraalVM" >nul
if errorlevel 1 (
    echo ERROR: GraalVM not detected!
    echo Please install GraalVM and ensure JAVA_HOME points to it.
    echo See BUILD-NATIVE.md for installation instructions.
    pause
    exit /b 1
)

REM Check if native-image is installed
where native-image >nul 2>&1
if errorlevel 1 (
    echo ERROR: native-image tool not found!
    echo Run: gu install native-image
    pause
    exit /b 1
)

echo [✓] GraalVM detected
echo [✓] native-image tool found
echo.

echo Cleaning previous builds...
call mvn clean
if errorlevel 1 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo Building native executable...
echo This may take 2-5 minutes on first build...
echo.

call mvn package -Pnative
if errorlevel 1 (
    echo.
    echo ERROR: Native build failed!
    echo Check the error messages above for details.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build Successful!
echo ========================================
echo.
echo Executable location: target\bus-reservation-client.exe
echo.
echo To test the executable:
echo   .\target\bus-reservation-client.exe
echo.
echo To use custom API URL:
echo   .\target\bus-reservation-client.exe http://your-api-url:8080/bus-reservation
echo.

pause
