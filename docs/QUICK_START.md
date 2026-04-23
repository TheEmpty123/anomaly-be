# Quick Start Guide - Anomaly Detection API

## 5-Minute Setup

### 1️⃣ Open SSH Tunnel (Terminal 1)
```powershell
ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721
# Password: 1
```

### 2️⃣ Run FastAPI Server (Terminal 2)
```powershell
cd C:\Users\toang\Downloads\Study\anomaly\backend\flaskAPI
python main.py
```

### 3️⃣ Test Endpoints
```bash
curl http://localhost:8000/api/anomalies/VIX
http://localhost:8000/api/docs  # Swagger UI
```

## API Examples

### Get VIX Anomaly
```bash
curl http://localhost:8000/api/anomalies/VIX
```

### Get All Anomalies
```bash
curl http://localhost:8000/api/anomalies
```

### Filter High Risk
```bash
curl http://localhost:8000/api/anomalies?risk_level=High
```

## JavaScript Integration

```javascript
fetch('http://localhost:8000/api/anomalies/VIX')
  .then(r => r.json())
  .then(data => console.log(data))
```

## More Information

👉 See other files in docs/ folder for complete documentation

