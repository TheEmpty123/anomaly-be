"""
Mock Data Loader - Load anomaly data từ mock-data.json
Dùng cho Frontend dev song song mà không bị block chờ DB thực tế
"""
import json
import logging
from pathlib import Path
from typing import List, Optional, Dict, Any

logger = logging.getLogger(__name__)


def load_mock_data() -> List[Dict[str, Any]]:
    """
    Load dữ liệu giả từ mock-data.json
    
    Returns:
        List[Dict]: Danh sách các anomaly objects
    """
    try:
        mock_file = Path(__file__).parent.parent / "mock-data.json"
        
        if not mock_file.exists():
            logger.warning(f"Mock file not found: {mock_file}")
            return []
        
        with open(mock_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
            logger.info(f"✓ Loaded {len(data)} mock anomalies from {mock_file.name}")
            return data
            
    except json.JSONDecodeError as e:
        logger.error(f"Invalid JSON in mock-data.json: {str(e)}")
        return []
    except Exception as e:
        logger.error(f"Error loading mock data: {str(e)}")
        return []


def get_mock_anomaly(ticker: str) -> Optional[Dict[str, Any]]:
    """
    Lấy mock data cho một ticker cụ thể (case-insensitive)
    
    Args:
        ticker: Mã chứng chỉ (VIX, HAG, FPT, ...)
        
    Returns:
        Dict hoặc None nếu không tìm thấy
    """
    data = load_mock_data()
    ticker_upper = ticker.upper().strip()
    
    for item in data:
        if item.get('symbol', '').upper() == ticker_upper:
            logger.info(f"Found mock data for ticker: {ticker}")
            return item
    
    logger.warning(f"Mock data not found for ticker: {ticker}")
    return None


def get_mock_anomalies_by_risk_level(risk_level: str) -> List[Dict[str, Any]]:
    """
    Lấy danh sách mock anomalies lọc theo risk level
    
    Args:
        risk_level: Mức độ rủi ro (Low, High, Critical)
        
    Returns:
        List[Dict]: Danh sách anomalies phù hợp
    """
    data = load_mock_data()
    risk_level_upper = risk_level.upper().strip()
    
    filtered = [
        item for item in data
        if item.get('ai_analysis', {}).get('risk_level', '').upper() == risk_level_upper
    ]
    
    logger.info(f"Found {len(filtered)} mock anomalies with risk level: {risk_level}")
    return filtered


def get_available_tickers() -> List[str]:
    """
    Lấy danh sách các ticker có trong mock data
    
    Returns:
        List[str]: Danh sách ticker symbols
    """
    data = load_mock_data()
    tickers = [item.get('symbol') for item in data if 'symbol' in item]
    return list(set(tickers))  # Remove duplicates

