# API Reference

## Stellar API - `/stellar-api/v1`

### GET /stellar-api/v1/market-structure
Latest market structure snapshot by timeframe.

**Parameters:**
- `timeframe` *(required)*: `1M`, `3M`, `6M`, `1Y`
- `benchmark` *(optional)*: defaults to `VNINDEX`

**Response (200):**
```json
{
  "dateSk": 20240606,
  "timeframe": "1M",
  "benchmark": "VNINDEX",
  "marketStructureCode": "CODE123",
  "marketStructureLabel": "Label",
  "coreSectors": "[\"BANK\", \"TECH\"]",
  "coreBlocks": "[\"Block1\"]",
  "topEcosystemCode": "ECO1",
  "topEcosystemName": "Ecosystem 1",
  "sectorRankings": "[...]",
  "ecosystemRankings": "[...]",
  "ingestionTime": "2024-06-06T10:00:00Z"
}
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---

### GET /stellar-api/v1/rrg
Sector RRG items by regime.

**Parameters:**
- `regime` *(required)*: `VENTURE`, `FLEXIBLE`, `ENDURING`
- `benchmark` *(optional)*: defaults to `VNINDEX`
- `dateSk` *(optional)*: uses latest if omitted

**Response (200):**
```json
{
  "items": [
    {
      "sectorCode": "BANK",
      "sectorName": "Banking",
      "sectorNameEn": "Banking Sector",
      "dateSk": 20240606,
      "rs": 1.25,
      "rm": 0.85,
      "phase": "Leading",
      "blockType": "Core",
      "stockCount": 15,
      "totalStocks": 20,
      "topStocksByCap": "[...]",
      "benchmark": "VNINDEX",
      "regime": "VENTURE",
      "ingestionTime": "2024-06-06T10:00:00Z"
    }
  ]
}
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---

### GET /stellar-api/v1/sector-performance
Latest sector performance by timeframe.

**Parameters:**
- `timeframe` *(required)*: `1M`, `3M`, `6M`, `1Y`
- `sectorCode` *(optional)*: filter by sector

**Response (200):**
```json
[
  {
    "sectorCode": "BANK",
    "blockType": "Core",
    "timeframe": "1M",
    "chartData": "[...]",
    "ingestionTime": "2024-06-06T10:00:00Z"
  }
]
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---