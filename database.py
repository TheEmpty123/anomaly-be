"""
Database connection module - PostgreSQL connection pooling
"""
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker, Session
from sqlalchemy.pool import NullPool
from config import get_settings
import logging

logger = logging.getLogger(__name__)
settings = get_settings()

# === DATA MART CONNECTION (Cho Frontend API) ===
DM_URL = f"postgresql://{settings.DM_USER}:{settings.DM_PASSWORD}@{settings.DM_HOST}:{settings.DM_PORT}/{settings.DM_NAME}"

dm_engine = create_engine(
    DM_URL,
    poolclass=NullPool,  # Avoid connection leak
    echo=settings.DEBUG,  # Log SQL queries if DEBUG=True
    connect_args={"timeout": 10, "application_name": "anomaly_api"}
)

DMSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=dm_engine)

# === DATA WAREHOUSE CONNECTION (Cho AI lấy data OHLCV) ===
dwh_user = settings.DWH_USER
dwh_pass = settings.DWH_PASSWORD
dwh_host = settings.DWH_HOST
dwh_port = settings.DWH_PORT
dwh_name = settings.DWH_NAME
DWH_URL = f"postgresql://{dwh_user}:{dwh_pass}@{dwh_host}:{dwh_port}/{dwh_name}"

dwh_engine = create_engine(
    DWH_URL,
    poolclass=NullPool,
    echo=settings.DEBUG,
    connect_args={"timeout": 10, "application_name": "anomaly_ai"}
)

DWHSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=dwh_engine)


def get_dm_db() -> Session:
    """Dependency injection for Data Mart DB session (Frontend API)"""
    db = DMSessionLocal()
    try:
        yield db
    finally:
        db.close()


def get_dwh_db() -> Session:
    """Dependency injection for Data Warehouse DB session (AI Processing)"""
    db = DWHSessionLocal()
    try:
        yield db
    finally:
        db.close()


def test_dm_connection() -> bool:
    """Test Data Mart connection"""
    try:
        with dm_engine.connect() as conn:
            result = conn.execute(text("SELECT 1"))
            logger.info("✓ Data Mart (DM) connection successful")
            return True
    except Exception as e:
        logger.error(f"✗ Data Mart connection failed: {str(e)}")
        return False


def test_dwh_connection() -> bool:
    """Test Data Warehouse connection"""
    try:
        with dwh_engine.connect() as conn:
            result = conn.execute(text("SELECT 1"))
            logger.info("✓ Data Warehouse (DWH) connection successful")
            return True
    except Exception as e:
        logger.error(f"✗ Data Warehouse connection failed: {str(e)}")
        return False

