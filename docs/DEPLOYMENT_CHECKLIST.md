# Deployment & Setup Checklist

## ✅ Phase 1: Initial Setup (COMPLETED)

### Infrastructure
- [x] Python 3.10+ installed
- [x] Virtual environment created
- [x] Dependencies installed

### Configuration
- [x] .env file created
- [x] Database connections configured
- [x] SSH Tunnel documented

### Application
- [x] FastAPI framework setup
- [x] 7 API endpoints working
- [x] Mock data operational
- [x] Error handling implemented

## 🚀 Daily Operation

### Before Starting

**Terminal 1: SSH Tunnel**
```powershell
ssh -N -L 5432:172.17.0.1:5432 dev-1@0.tcp.ap.ngrok.io -p 12721
# Password: 1
```

**Terminal 2: FastAPI Server**
```powershell
python main.py
```

### Testing
```powershell
curl http://localhost:8000/api/health
curl http://localhost:8000/api/anomalies/VIX
.\test-api.bat
```

## 🛠️ Troubleshooting

### Connection refused
→ Start SSH tunnel first

### Port 8000 in use
```powershell
netstat -ano | findstr :8000
taskkill /PID <PID> /F
```

### Module not found
```powershell
pip install -r requirements.txt
```

## 📋 Production Checklist

- [ ] Set DEBUG=False in .env
- [ ] Enable HTTPS/SSL
- [ ] Configure CORS properly
- [ ] Setup logging
- [ ] Add rate limiting
- [ ] Deploy to cloud
- [ ] Setup monitoring

See README.md for complete guide.

