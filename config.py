"""
Configuration module - Read environment variables from .env
"""
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """Application settings from .env file"""
    
    # Data Warehouse (DWH) - Dành cho AI lấy data OHLCV
    DWH_USER: str
    DWH_PASSWORD: str
    DWH_HOST: str
    DWH_PORT: int
    DWH_NAME: str
    DWH_URL: str = ""
    
    # Data Mart (DM) - Dành cho FastAPI phục vụ Frontend
    DM_USER: str
    DM_PASSWORD: str
    DM_HOST: str
    DM_PORT: int
    DM_NAME: str
    DM_URL: str = ""
    
    # FastAPI Config
    DEBUG: bool = False
    API_PREFIX: str = "/api"
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = True


@lru_cache()
def get_settings() -> Settings:
    """Get cached settings instance"""
    return Settings()

