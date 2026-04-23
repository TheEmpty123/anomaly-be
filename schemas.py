"""
Pydantic schemas for request/response validation
"""
from pydantic import BaseModel, Field
from typing import List, Optional


class AIAnalysis(BaseModel):
    """AI Analysis results for anomaly detection"""
    anomaly_score: float = Field(..., description="Điểm dị thường (0-1)")
    threshold: float = Field(..., description="Ngưỡng phát hiện")
    is_anomaly: bool = Field(..., description="Có phải là dị thường?")
    status_label: str = Field(..., description="Nhãn trạng thái (BÌNH THƯỜNG, CẢNH BÁO, THAO TÚNG)")
    risk_level: str = Field(..., description="Mức độ rủi ro (Low, High, Critical)")

    class Config:
        json_schema_extra = {
            "example": {
                "anomaly_score": 0.88,
                "threshold": 0.70,
                "is_anomaly": True,
                "status_label": "CẢNH BÁO",
                "risk_level": "High"
            }
        }


class RawFeatures(BaseModel):
    """Raw market features for anomaly calculation"""
    price_change_pct: float = Field(..., description="% thay đổi giá")
    volume_ratio: float = Field(..., description="Tỷ lệ khối lượng so với TB 20 phiên")
    amplitude_pct: float = Field(..., description="Biên độ giao dịch trong phiên (%)")

    class Config:
        json_schema_extra = {
            "example": {
                "price_change_pct": 0.2,
                "volume_ratio": 3.8,
                "amplitude_pct": 1.5
            }
        }


class AnomalyResponse(BaseModel):
    """Main anomaly response model"""
    symbol: str = Field(..., description="Mã chứng chỉ (VIX, HAG, FPT, ...)")
    date: str = Field(..., description="Ngày phát hiện (YYYY-MM-DD)")
    ai_analysis: AIAnalysis = Field(..., description="Kết quả phân tích AI")
    raw_features: RawFeatures = Field(..., description="Dữ liệu thô từ thị trường")
    explanation: str = Field(..., description="Giải thích chi tiết bằng Tiếng Việt")

    class Config:
        json_schema_extra = {
            "example": {
                "symbol": "VIX",
                "date": "2026-04-22",
                "ai_analysis": {
                    "anomaly_score": 0.88,
                    "threshold": 0.70,
                    "is_anomaly": True,
                    "status_label": "CẢNH BÁO",
                    "risk_level": "High"
                },
                "raw_features": {
                    "price_change_pct": 0.2,
                    "volume_ratio": 3.8,
                    "amplitude_pct": 1.5
                },
                "explanation": "Khối lượng giao dịch cao gấp 3.8 lần trung bình 20 phiên, nhưng giá gần như không đổi (+0.2%). Dấu hiệu có lệnh trao tay hoặc tổ chức gom hàng khối lượng lớn."
            }
        }


class HealthCheckResponse(BaseModel):
    """Health check response"""
    status: str = Field(..., description="Service status")
    service: str = Field(..., description="Service name")
    dm_connected: bool = Field(..., description="Data Mart connection status")
    dwh_connected: bool = Field(..., description="Data Warehouse connection status")


class ErrorResponse(BaseModel):
    """Error response model"""
    detail: str = Field(..., description="Error message")
    error_code: Optional[str] = Field(None, description="Error code for debugging")

