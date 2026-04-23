# 🔍 Anomaly Detection API

Backend FastAPI để phát hiện dị thường trong dữ liệu thị trường chứng chỉ với kết nối PostgreSQL an toàn.

## 📋 Mục tiêu (User Story)

Là một Backend Developer, tôi cần:
- ✅ Thiết lập bộ khung FastAPI
- ✅ Cấu hình kết nối an toàn tới PostgreSQL (DWH + DM)
- ✅ Tạo API trả về dữ liệu giả (Mock JSON) 
- ✅ Frontend có thể dev song song ngay lập tức mà không bị block

## 🏗️ Cấu trúc Project

```
flaskAPI/
├── main.py                    # Entry point - Chạy server FastAPI
├── config.py                  # Cấu hình từ .env (pydantic-settings)
├── database.py                # Kết nối PostgreSQL (DM + DWH)
├── schemas.py                 # Pydantic models để validate request/response
├── requirements.txt           # Python dependencies
├── .env                        # Biến môi trường (SSH Tunnel, DB credentials)
├── mock-data.json            # Dữ liệu giả cho Frontend dev
│
├── api/
│   ├── __init__.py
│   └── routes.py             # API endpoints (/api/anomalies/*)
│
├── utils/
│   ├── __init__.py
│   └── mock_loader.py        # Hàm load mock data từ mock-data.json
│
└── README.md                  # Tài liệu này
```

## 🚀 Bắt Đầu Nhanh

### 1️⃣ Yêu Cầu

- Python 3.10+
- PostgreSQL (có thể truy cập qua SSH Tunnel)
- SSH client (để mở tunnel đến ngrok.io)

### 2️⃣ Cài Đặt Dependencies

```powershell
pip install -r requirements.txt
```

### 3️⃣ Mở SSH Tunnel (Terminal 1)

Chạy lệnh này **trước** khi start server:

```powershell
ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721
```

**Khi được hỏi password, nhập:** `1`

⚠️ **Giữ terminal này chạy liên tục!**

### 4️⃣ Chạy FastAPI Server (Terminal 2)

```powershell
cd C:\Users\toang\Downloads\Study\anomaly\backend\flaskAPI
python main.py
```

**Kết quả:**
```
🚀 Starting Anomaly Detection API...
📍 Debug Mode: False
📍 API Prefix: /api

🔍 Testing Database Connections...
✓ Data Mart (DM) connection successful
✓ Data Warehouse (DWH) connection successful

INFO:     Uvicorn running on http://0.0.0.0:8000
```

## 📚 API Endpoints

### 🏥 Health Check

```bash
GET http://localhost:8000/api/health
```

**Response:**
```json
{
  "status": "healthy",
  "service": "Anomaly Detection API",
  "dm_connected": true,
  "dwh_connected": true
}
```

### 📊 Lấy Anomaly cho Ticker Cụ Thể

```bash
GET http://localhost:8000/api/anomalies/{ticker}
```

**Example:**
```bash
curl http://localhost:8000/api/anomalies/VIX
```

**Response:**
```json
{
  "symbol": "VIX",
  "date": "2026-04-22",
  "ai_analysis": {
    "anomaly_score": 0.88,
    "threshold": 0.70,
    "is_anomaly": true,
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
```

### 📋 Lấy Tất Cả Anomalies

```bash
GET http://localhost:8000/api/anomalies
```

### 🔍 Lọc theo Risk Level

```bash
GET http://localhost:8000/api/anomalies?risk_level=High
GET http://localhost:8000/api/anomalies?risk_level=Critical
```

### ℹ️ Lấy Danh Sách Ticker Có Sẵn

```bash
GET http://localhost:8000/api/info/tickers
```

**Response:**
```json
["FPT", "HAG", "VIX"]
```

### ✅ Verify Anomaly (Placeholder)

```bash
POST http://localhost:8000/api/anomalies/{ticker}/verify?is_valid=true
```

**Response:**
```json
{
  "ticker": "VIX",
  "symbol": "VIX",
  "verification_result": true,
  "message": "✓ Verification recorded in mock mode (DB integration pending)",
  "note": "Data sẽ được lưu vào bảng 'anomaly_feedback' trong Data Mart sau khi integrate DB"
}
```

## 📖 Swagger UI Documentation

Truy cập tại: **http://localhost:8000/api/docs**

- Xem chi tiết tất cả endpoints
- Test API trực tiếp
- Xem request/response examples

## 🗄️ Cấu Hình Database

### File `.env` - Biến Môi Trường

```dotenv
# 0. SSH TUNNEL
ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721
# Password: 1

# 1. DATA WAREHOUSE (Cho AI lấy data OHLCV)
DWH_USER=readonly_api2
DWH_PASSWORD=dataforai
DWH_HOST=localhost          # SSH Tunnel forward to 5432
DWH_PORT=5432
DWH_NAME=core_data_warehouse

# 2. DATA MART (Cho Frontend API)
DM_USER=api_readonly
DM_PASSWORD=thisisapassword
DM_HOST=localhost           # SSH Tunnel forward to 5432
DM_PORT=5432
DM_NAME=stellar_dm
```

### Cơ Chế Kết Nối

```
FastAPI (localhost:8000)
    ↓
    ├─→ Data Mart (DM) - stellar_dm
    │   └─→ Phục vụ Frontend API (/api/anomalies/*)
    │
    └─→ Data Warehouse (DWH) - core_data_warehouse
        └─→ Lấy OHLCV data cho AI Processing
```

## 🎯 Current Mode: Mock Data

### ✅ Hiện Tại
- API sử dụng **Mock Data** từ `mock-data.json`
- Frontend có thể dev ngay mà không cần DB
- Không bị block chờ DB connection

### 🔮 Tương Lai (Integration)
```python
# Hiện tại (routes.py)
anomaly = get_mock_anomaly(ticker)  # ← Mock

# Tương lai
# anomaly = db.query(Anomaly).filter(Anomaly.symbol == ticker).first()  # ← DB
```

## 🔗 Frontend Integration

Frontend team có thể ngay lập tức:

### 1. Lấy dữ liệu dị thường
```javascript
fetch('http://localhost:8000/api/anomalies/VIX')
  .then(r => r.json())
  .then(data => console.log(data))
```

### 2. Lọc theo risk level
```javascript
fetch('http://localhost:8000/api/anomalies?risk_level=High')
  .then(r => r.json())
  .then(data => console.log(data))
```

### 3. Xem tất cả ticker
```javascript
fetch('http://localhost:8000/api/info/tickers')
  .then(r => r.json())
  .then(tickers => console.log(tickers))
```

## 🐛 Debugging

### Logs

Server logs sẽ show:
```
INFO:     Request: GET /api/anomalies/VIX
DEBUG:    → GET /api/anomalies/VIX
INFO:     Fetched anomaly data for ticker: VIX
DEBUG:    ← 200
```

### Enable Debug Mode

Sửa trong `main.py`:
```python
settings.DEBUG = True
```

Hoặc trong `.env`:
```dotenv
DEBUG=True
```

## 📦 Dependencies

| Package | Version | Mục đích |
|---------|---------|---------|
| fastapi | 0.104.1 | Web framework |
| uvicorn | 0.24.0 | ASGI server |
| sqlalchemy | 2.0.23 | ORM |
| psycopg2-binary | 2.9.9 | PostgreSQL driver |
| pydantic | 2.5.0 | Data validation |
| pydantic-settings | 2.1.0 | Environment config |
| python-dotenv | 1.0.0 | Load .env file |

## 🚨 Troubleshooting

### ❌ "Connection refused" - PostgreSQL

**Giải pháp:**
1. Kiểm tra SSH Tunnel chạy chưa?
   ```powershell
   ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721
   ```
2. Kiểm tra port 5432 đã được forward?
   ```powershell
   netstat -an | findstr 5432
   ```

### ❌ "ModuleNotFoundError"

**Giải pháp:**
```powershell
pip install -r requirements.txt
```

### ❌ Port 8000 already in use

**Giải pháp:**
```powershell
# Tìm process đang dùng port 8000
netstat -ano | findstr :8000

# Kill process (replace PID)
taskkill /PID <PID> /F
```

## 📝 Next Steps - Roadmap

### Phase 1: ✅ Mock Setup (Hiện tại)
- [x] FastAPI framework
- [x] PostgreSQL cấu hình
- [x] Mock JSON endpoints
- [x] Swagger documentation

### Phase 2: 🔮 Database Integration
- [ ] Create SQLAlchemy models
- [ ] Implement ORM queries
- [ ] Replace mock_loader with DB queries
- [ ] Add database migrations

### Phase 3: 🤖 AI Processing
- [ ] Integrate anomaly detection model
- [ ] Add feature engineering pipeline
- [ ] Implement async AI processing

### Phase 4: 📊 Real-time Updates
- [ ] WebSocket support
- [ ] Real-time anomaly alerts
- [ ] Data streaming pipeline

## 🤝 Contributing

Khi integrate database:
1. Update `routes.py` - Replace mock với DB queries
2. Keep same response schema
3. Maintain API compatibility

## 📞 Support

- SSH Tunnel issue: Check ngrok connection
- DB connection: Verify `.env` credentials
- API issue: Check `/api/docs` documentation
- Logs: Check console output

---

**Version:** 1.0.0  
**Status:** Development (Mock Mode)  
**Last Updated:** 2026-04-23

