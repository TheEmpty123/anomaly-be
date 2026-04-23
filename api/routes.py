"""
API Routes - Anomaly Detection Endpoints
"""
from fastapi import APIRouter, HTTPException, Depends, Query, Path
from sqlalchemy.orm import Session
from typing import List, Optional
import logging

from schemas import AnomalyResponse, HealthCheckResponse
from utils.mock_loader import get_mock_anomaly, load_mock_data, get_mock_anomalies_by_risk_level, get_available_tickers
from database import get_dm_db, test_dm_connection, test_dwh_connection

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api", tags=["anomalies"])


@router.get("/health", response_model=HealthCheckResponse, summary="🏥 Health Check")
async def health_check():
    """
    Kiểm tra trạng thái API và kết nối cơ sở dữ liệu
    
    - **status**: Trạng thái của API
    - **dm_connected**: Kết nối Data Mart (cho Frontend API)
    - **dwh_connected**: Kết nối Data Warehouse (cho AI Processing)
    """
    dm_ok = test_dm_connection()
    dwh_ok = test_dwh_connection()
    
    status = "healthy" if (dm_ok or dwh_ok) else "unhealthy"
    
    return {
        "status": status,
        "service": "Anomaly Detection API",
        "dm_connected": dm_ok,
        "dwh_connected": dwh_ok
    }


@router.get("/anomalies/{ticker}", response_model=AnomalyResponse, summary="📊 Get Anomaly by Ticker")
async def get_anomaly_by_ticker(
    ticker: str = Path(
        ..., 
        description="Stock ticker symbol (VIX, HAG, FPT, ...)",
        example="VIX",
        min_length=1,
        max_length=10
    ),
    db: Session = Depends(get_dm_db)
):
    """
    Trả về dữ liệu dị thường cho một mã chứng chỉ cụ thể.
    
    **📌 Hiện tại**: Sử dụng Mock Data (mock-data.json) để Frontend dev song song
    **🔮 Tương lai**: Sẽ kết nối trực tiếp tới bảng anomalies trong Data Mart (DM)
    
    **Parameters:**
    - **ticker**: Mã chứng chỉ (ví dụ: VIX, HAG, FPT)
    
    **Response:**
    - Anomaly detection results với AI analysis score, risk level, và giải thích chi tiết
    
    **Example:**
    ```
    GET /api/anomalies/VIX
    ```
    
    **Available Tickers:**
    - VIX, HAG, FPT (from mock-data.json)
    """
    
    # Mock Mode: Lấy dữ liệu từ mock-data.json
    anomaly = get_mock_anomaly(ticker)
    
    if not anomaly:
        available = get_available_tickers()
        raise HTTPException(
            status_code=404,
            detail=f"Không tìm thấy dữ liệu cho ticker '{ticker}'. Available: {', '.join(available) if available else 'None'}"
        )
    
    logger.info(f"Fetched anomaly data for ticker: {ticker}")
    return anomaly


@router.get("/anomalies", response_model=List[AnomalyResponse], summary="📋 Get All Anomalies")
async def get_all_anomalies(
    risk_level: Optional[str] = Query(
        None, 
        description="Filter by risk level",
        enum=["Low", "High", "Critical"],
        example="High"
    ),
    db: Session = Depends(get_dm_db)
):
    """
    Trả về tất cả dữ liệu dị thường (Mock Data).
    
    **📌 Hiện tại**: Sử dụng Mock Data (mock-data.json)
    **🔮 Tương lai**: Sẽ query trực tiếp từ bảng anomalies trong Data Mart (DM)
    
    **Query Parameters:**
    - **risk_level** (optional): Lọc theo mức độ rủi ro
      - `Low`: Rủi ro thấp
      - `High`: Rủi ro cao
      - `Critical`: Rủi ro gọi
    
    **Examples:**
    ```
    GET /api/anomalies                           # Tất cả dị thường
    GET /api/anomalies?risk_level=High           # Chỉ High risk
    GET /api/anomalies?risk_level=Critical       # Chỉ Critical
    ```
    """
    
    if risk_level:
        # Lọc theo risk level
        data = get_mock_anomalies_by_risk_level(risk_level)
        logger.info(f"Fetched {len(data)} anomalies with risk_level: {risk_level}")
    else:
        # Trả về tất cả
        data = load_mock_data()
        logger.info(f"Fetched all {len(data)} anomalies")
    
    return data


@router.post("/anomalies/{ticker}/verify", summary="✅ Verify Anomaly (Placeholder)")
async def verify_anomaly(
    ticker: str = Path(..., description="Stock ticker symbol"),
    is_valid: bool = Query(..., description="True = xác nhận hợp lệ, False = xác nhận sai"),
    db: Session = Depends(get_dm_db)
):
    """
    Placeholder endpoint để verify/confirm dị thường.
    
    **⚠️ TODO**: Kết nối tới bảng verification_logs hoặc anomaly_feedback trong Data Mart
    
    **Parameters:**
    - **ticker**: Mã chứng chỉ
    - **is_valid**: True/False - xác nhận tính hợp lệ của dị thường
    
    **Future:**
    - Lưu feedback vào DB
    - Dùng để train lại model AI
    - Improve accuracy của anomaly detection
    """
    
    anomaly = get_mock_anomaly(ticker)
    
    if not anomaly:
        raise HTTPException(status_code=404, detail=f"Ticker '{ticker}' not found")
    
    logger.info(f"Verification for {ticker}: is_valid={is_valid}")
    
    return {
        "ticker": ticker,
        "symbol": anomaly.get('symbol'),
        "verification_result": is_valid,
        "message": "✓ Verification recorded in mock mode (DB integration pending)",
        "note": "Data sẽ được lưu vào bảng 'anomaly_feedback' trong Data Mart sau khi integrate DB"
    }


@router.get("/info/tickers", response_model=List[str], summary="ℹ️ Get Available Tickers")
async def get_info_tickers():
    """
    Lấy danh sách các ticker hiện có trong mock data.
    
    **Useful cho Frontend:**
    - Populate dropdown/select list
    - Validate user input
    - Show available options
    """
    
    tickers = get_available_tickers()
    logger.info(f"Available tickers: {tickers}")
    
    return sorted(tickers)

