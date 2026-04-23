"""
FastAPI Application Entry Point
Anomaly Detection API with PostgreSQL Backend
"""
import logging
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
import uvicorn

from config import get_settings
from api.routes import router
from database import test_dm_connection, test_dwh_connection

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

settings = get_settings()


# ============================================================================
# STARTUP / SHUTDOWN EVENTS
# ============================================================================

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Lifespan context manager for startup/shutdown events
    """
    # === STARTUP ===
    logger.info("🚀 Starting Anomaly Detection API...")
    logger.info(f"📍 Debug Mode: {settings.DEBUG}")
    logger.info(f"📍 API Prefix: {settings.API_PREFIX}")
    
    # Test database connections
    logger.info("\n🔍 Testing Database Connections...")
    dm_ok = test_dm_connection()
    dwh_ok = test_dwh_connection()
    
    if not (dm_ok or dwh_ok):
        logger.warning("⚠️  No database connections available - Running in mock-only mode")
    else:
        logger.info("✓ Database connections OK")
    
    yield
    
    # === SHUTDOWN ===
    logger.info("\n🛑 Shutting down Anomaly Detection API...")
    logger.info("✓ Graceful shutdown complete")


# ============================================================================
# APP INITIALIZATION
# ============================================================================

app = FastAPI(
    title="🔍 Anomaly Detection API",
    description="""
    Backend API for detecting market anomalies in securities data.
    
    **Features:**
    - 📊 Real-time anomaly detection
    - 🤖 AI-powered analysis
    - 📈 Market data integration (OHLCV)
    - 🔒 Secure PostgreSQL backend
    - 📝 Comprehensive API documentation
    
    **Current Mode:**
    - 🟢 Mock Data Mode (Frontend dev-friendly)
    - 🔮 Database Mode (Coming Soon)
    
    **Tech Stack:**
    - FastAPI
    - SQLAlchemy ORM
    - PostgreSQL (Data Mart + Data Warehouse)
    - Pydantic validation
    """,
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
    openapi_url="/api/openapi.json",
    lifespan=lifespan
)

# ============================================================================
# MIDDLEWARE
# ============================================================================

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # ⚠️ Allow all origins for development
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Request logging middleware
@app.middleware("http")
async def log_requests(request: Request, call_next):
    """Log incoming requests"""
    logger.debug(f"→ {request.method} {request.url.path}")
    response = await call_next(request)
    logger.debug(f"← {response.status_code}")
    return response

# ============================================================================
# ROUTES
# ============================================================================

# Include API routes
app.include_router(router)


@app.get("/", tags=["Root"], summary="Root Endpoint")
async def root():
    """
    Welcome endpoint with API information
    """
    return {
        "message": "👋 Welcome to Anomaly Detection API",
        "version": "1.0.0",
        "status": "🟢 Running",
        "docs_url": "http://localhost:8000/api/docs",
        "redoc_url": "http://localhost:8000/api/redoc",
        "health_check": "http://localhost:8000/api/health",
        "get_all_anomalies": "http://localhost:8000/api/anomalies",
        "get_anomaly_by_ticker": "http://localhost:8000/api/anomalies/{ticker}",
        "available_tickers": "http://localhost:8000/api/info/tickers"
    }


@router.get("/version", tags=["Info"], summary="API Version Info")
async def get_version():
    """Get API version information"""
    return {
        "version": "1.0.0",
        "status": "development",
        "mode": "mock_data (Frontend-ready)",
        "next_milestone": "Database integration"
    }


# ============================================================================
# ERROR HANDLERS
# ============================================================================

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """Global exception handler"""
    logger.error(f"Unhandled exception: {str(exc)}")
    
    return JSONResponse(
        status_code=500,
        content={
            "detail": str(exc),
            "error_code": "INTERNAL_SERVER_ERROR"
        }
    )


@app.exception_handler(404)
async def not_found_handler(request: Request, exc: Exception):
    """Handle 404 Not Found"""
    return JSONResponse(
        status_code=404,
        content={
            "detail": f"Endpoint '{request.url.path}' not found",
            "error_code": "NOT_FOUND"
        }
    )


# ============================================================================
# MAIN - RUN SERVER
# ============================================================================

if __name__ == "__main__":
    logger.info("=" * 80)
    logger.info("🚀 STARTING ANOMALY DETECTION API")
    logger.info("=" * 80)
    logger.info("\n📌 Configuration:")
    logger.info(f"   - Debug Mode: {settings.DEBUG}")
    logger.info(f"   - API Prefix: {settings.API_PREFIX}")
    logger.info(f"   - DM Connection: {settings.DM_HOST}:{settings.DM_PORT}/{settings.DM_NAME}")
    logger.info(f"   - DWH Connection: {settings.DWH_HOST}:{settings.DWH_PORT}/{settings.DWH_NAME}")
    logger.info("\n📚 Documentation:")
    logger.info("   - Swagger UI: http://localhost:8000/api/docs")
    logger.info("   - ReDoc: http://localhost:8000/api/redoc")
    logger.info("\n⚠️  SSH Tunnel (Chạy trước trong terminal khác):")
    logger.info("   ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721")
    logger.info("   Password: 1")
    logger.info("\n" + "=" * 80 + "\n")
    
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        reload=settings.DEBUG,
        log_level="info"
    )

