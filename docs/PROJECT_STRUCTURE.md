# Project Structure & File Documentation

## 🏗️ Directory Structure

```
flaskAPI/
├── main.py                      ← FastAPI entry point
├── config.py                    ← Environment config
├── database.py                  ← PostgreSQL connections
├── schemas.py                   ← Pydantic models
├── requirements.txt             ← Dependencies
├── .env                         ← Database credentials
├── .gitignore                   ← Git ignore rules
├── mock-data.json              ← Sample data
├── test-api.bat                ← Test script
│
├── api/
│   └── routes.py               ← 7 API endpoints
│
├── utils/
│   └── mock_loader.py          ← Mock data loader
│
├── docs/                        ← Documentation folder
│   ├── START_HERE.md
│   ├── QUICK_START.md
│   ├── README.md
│   ├── PROJECT_STRUCTURE.md
│   ├── COMPLETION_SUMMARY.md
│   ├── DEPLOYMENT_CHECKLIST.md
│   └── DOCUMENTATION_INDEX.md
│
└── README.md                   ← Main reference (root)
```

## 📄 File Descriptions

### Application Files

**main.py (6,720 bytes)**
- FastAPI app initialization
- Router registration
- Middleware setup
- Startup/shutdown events
- Error handlers

**config.py (922 bytes)**
- Load environment variables
- Database credentials
- Settings management

**database.py (2,562 bytes)**
- PostgreSQL connections
- Data Mart (DM) engine
- Data Warehouse (DWH) engine
- Session makers
- Connection testing

**schemas.py (3,513 bytes)**
- Pydantic validation models
- AIAnalysis
- RawFeatures
- AnomalyResponse
- HealthCheckResponse

**api/routes.py**
- 7 API endpoints
- Request/response handling
- Mock data integration

**utils/mock_loader.py**
- Load mock data from JSON
- Filter by risk level
- Get available tickers

## 🔄 Data Flow

```
Frontend Request
    ↓
FastAPI Router
    ↓
Validation (Pydantic)
    ↓
Mock Loader
    ↓
mock-data.json
    ↓
JSON Response
```

## 📊 Technology Stack

- **Framework:** FastAPI 0.104.1
- **Server:** Uvicorn 0.24.0
- **Database:** PostgreSQL
- **ORM:** SQLAlchemy 2.0.23
- **Validation:** Pydantic 2.5.0
- **Driver:** psycopg2-binary

See other docs for complete information.

