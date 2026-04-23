@echo off
REM ============================================================================
REM Anomaly Detection API - Test Script
REM Run this file to test all API endpoints
REM ============================================================================

echo.
echo ============================================================================
echo 🧪 ANOMALY DETECTION API - TEST SUITE
echo ============================================================================
echo.

setlocal enabledelayedexpansion

REM Check if server is running
echo 🔍 Checking if server is running...
timeout /t 1 /nobreak >nul
powershell -Command "try { $null = Invoke-WebRequest http://localhost:8000/ -UseBasicParsing -ErrorAction Stop; Write-Host '✓ Server is running!' } catch { Write-Host '✗ Server not running. Start it first: python main.py'; exit 1 }"
if %errorlevel% neq 0 (
    echo.
    echo Run in Terminal 2:
    echo.
    echo   cd C:\Users\toang\Downloads\Study\anomaly\backend\flaskAPI
    echo   python main.py
    echo.
    exit /b 1
)

echo.
echo.
echo ============================================================================
echo 📊 TEST 1: Root Endpoint
echo ============================================================================
curl http://localhost:8000/
echo.
echo.

echo ============================================================================
echo 🏥 TEST 2: Health Check
echo ============================================================================
curl http://localhost:8000/api/health
echo.
echo.

echo ============================================================================
echo 📊 TEST 3: Get VIX Anomaly
echo ============================================================================
curl http://localhost:8000/api/anomalies/VIX
echo.
echo.

echo ============================================================================
echo 📊 TEST 4: Get HAG Anomaly
echo ============================================================================
curl http://localhost:8000/api/anomalies/HAG
echo.
echo.

echo ============================================================================
echo 📊 TEST 5: Get FPT Anomaly
echo ============================================================================
curl http://localhost:8000/api/anomalies/FPT
echo.
echo.

echo ============================================================================
echo 📋 TEST 6: Get All Anomalies
echo ============================================================================
curl http://localhost:8000/api/anomalies
echo.
echo.

echo ============================================================================
echo 🔍 TEST 7: Filter High Risk Anomalies
echo ============================================================================
curl http://localhost:8000/api/anomalies?risk_level=High
echo.
echo.

echo ============================================================================
echo 🔍 TEST 8: Filter Critical Risk Anomalies
echo ============================================================================
curl http://localhost:8000/api/anomalies?risk_level=Critical
echo.
echo.

echo ============================================================================
echo ℹ️  TEST 9: Get Available Tickers
echo ============================================================================
curl http://localhost:8000/api/info/tickers
echo.
echo.

echo ============================================================================
echo ✅ All tests completed!
echo ============================================================================
echo.
echo 📚 Swagger UI: http://localhost:8000/api/docs
echo 📚 ReDoc: http://localhost:8000/api/redoc
echo.
pause

