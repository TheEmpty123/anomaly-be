# API Reference

## Stellar API - `/stellar-api/v1`

## Tổng hợp API hiện có
- Foreign Flow
  - GET /stellar-api/v1/foreign-flow/chart — Dữ liệu chuỗi thời gian theo mã cổ phiếu/ngành/thị trường
  - GET /stellar-api/v1/foreign-flow/heatmap — Dữ liệu heatmap theo ngày và timeframe
- Heatmap (Realtime qua SSE)
  - GET /stellar-api/v1/heatmap/snapshot — Ảnh chụp heatmap hiện tại (từ Redis)
  - GET /stellar-api/v1/heatmap/stream — Luồng sự kiện Server-Sent Events (SSE) cập nhật realtime
- OHLCV
  - GET /stellar-api/v1/ohlcv/{symbol} — Lịch sử OHLCV theo mã
  - GET /stellar-api/v1/ohlcv — OHLCV cho tất cả mã trong một ngày
  - GET /stellar-api/v1/ohlcv/latest — OHLCV mới nhất cho tất cả mã
- Symbols
  - GET /stellar-api/v1/symbols — Danh sách metadata cơ bản của mã
- Market Structure
  - GET /stellar-api/v1/market-structure — Ảnh chụp cấu trúc thị trường mới nhất
- RRG
  - GET /stellar-api/v1/rrg — Danh sách RRG ngành theo regime
- Sector Performance
  - GET /stellar-api/v1/sector-performance — Hiệu suất ngành theo timeframe

## Foreign Flow APIs

### GET /stellar-api/v1/foreign-flow/chart
Returns foreign flow time-series data by symbol, sector, or market. This supports line charts, cumulative foreign flow charts, and comparison between `priceIndex100` and `benchmarkIndex100`.

**Parameters:**
- `entityType` *(required)*: examples `SYMBOL`, `SECTOR`, `MARKET`
- `entityCode` *(required)*: examples `FPT`, `BANK`, `VNINDEX`
- `timeframe` *(required)*: examples `1D`, `1W`, `1M`, `3M`, `6M`, `1Y`
- `fromDateSk` *(optional)*: inclusive start date in `YYYYMMDD`
- `toDateSk` *(optional)*: inclusive end date in `YYYYMMDD`
- `limit` *(optional)*: defaults to `500`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/foreign-flow/chart?entityType=SYMBOL&entityCode=FPT&timeframe=1D&limit=200"
```

**Response (200):**
```json
[
  {
    "entityType": "SYMBOL",
    "entityCode": "FPT",
    "dateSk": 20260704,
    "timeframe": "1D",
    "buyVal": 12000000000,
    "sellVal": 9000000000,
    "netVal": 3000000000,
    "cumulativeNetVal": 15000000000,
    "buyVol": 100000,
    "sellVol": 80000,
    "netVol": 20000,
    "cumulativeNetVol": 120000,
    "close": 123400,
    "priceIndex100": 108.5,
    "benchmarkCode": "VNINDEX",
    "benchmarkClose": 1280.5,
    "benchmarkIndex100": 103.2,
    "ingestionTime": "2026-07-04T17:30:00"
  }
]
```

**Status Codes:** `200 OK` | `400 Bad Request`

---

### GET /stellar-api/v1/foreign-flow/heatmap
Returns foreign flow heatmap data by date and timeframe. This supports foreign flow heatmaps and optional filtering by net buy/sell direction.

**Parameters:**
- `dateSk` *(required)*: date in `YYYYMMDD`
- `timeframe` *(required)*: examples `1D`, `1W`, `1M`, `3M`, `6M`, `1Y`
- `direction` *(optional)*: `BUY`, `SELL`, `NEUTRAL`
- `limit` *(optional)*: defaults to `199`, max `1000`

**Examples:**
```bash
curl "http://localhost:8080/stellar-api/v1/foreign-flow/heatmap?dateSk=20260704&timeframe=1D&limit=199"
curl "http://localhost:8080/stellar-api/v1/foreign-flow/heatmap?dateSk=20260704&timeframe=1D&direction=BUY&limit=50"
```

**Response (200):**
```json
[
  {
    "dateSk": 20260704,
    "timeframe": "1D",
    "symbol": "FPT",
    "symbolSk": 123,
    "netVal": 3000000000,
    "cumulativeNetVal": 15000000000,
    "netVol": 20000,
    "cumulativeNetVol": 120000,
    "close": 123400,
    "pctChange": 1.25,
    "volume": 1000000,
    "value": 123400000000,
    "marketCap": 150000000000000,
    "marketWeight": 0.025,
    "rankNetBuy": 1,
    "rankNetSell": null,
    "intensity": 0.85,
    "direction": "BUY",
    "ingestionTime": "2026-07-04T17:30:00"
  }
]
```

**Status Codes:** `200 OK` | `400 Bad Request`

---

## Heatmap (Realtime SSE) APIs

### GET /stellar-api/v1/heatmap/snapshot
Trả về ảnh chụp dữ liệu Heatmap hiện tại được lưu trong Redis. Phù hợp để khởi tạo màn hình trước khi mở luồng realtime.

- Response: Array các đối tượng HeatmapQuoteDTO
- Status Codes: `200 OK`

Ví dụ gọi:
```bash
curl "http://localhost:8080/stellar-api/v1/heatmap/snapshot"
```

Ví dụ phản hồi (200):
```json
[
  {
    "symbol": "FPT",
    "price": 123400,
    "refPrice": 122000,
    "pctChange": 1.15,
    "volume": 1000000,
    "txnValue": 123400000000,
    "marketCap": 150000000000000,
    "status": "TRADING",
    "sector": "TECHNOLOGY",
    "industry": "Software",
    "exchange": "HOSE",
    "lastUpdated": "2026-07-04T18:59:30Z"
  }
]
```

Fields (tham khảo):
- symbol: mã cổ phiếu
- price: giá khớp hiện tại
- refPrice: giá tham chiếu
- pctChange: % thay đổi so với tham chiếu
- volume: khối lượng
- txnValue: giá trị giao dịch
- marketCap: vốn hóa
- status: trạng thái (TRADING, HALTED, ...)
- sector, industry, exchange: phân loại
- lastUpdated: thời điểm cập nhật ISO-8601

---

### GET /stellar-api/v1/heatmap/stream
Luồng Server-Sent Events (SSE) cung cấp cập nhật realtime cho Heatmap.

- Produces: `text/event-stream`
- Sự kiện gửi ra:
  - `ping`: heartbeat định kỳ để giữ kết nối (mỗi ~20s)
  - `quote`: bản tin giá/heatmap dạng JSON (string JSON hoặc object JSON)
- Timeout kết nối mặc định: ~30 phút (server sẽ đóng nếu không hoạt động). Client nên tự động reconnect.

Ví dụ JavaScript (trình duyệt):
```js
const es = new EventSource("/stellar-api/v1/heatmap/stream");

es.addEventListener("ping", (e) => {
  // e.data có thể là một JSON: { ts: "2026-07-04T19:00:00Z" }
  // dùng để cập nhật trạng thái kết nối
});

es.addEventListener("quote", (e) => {
  // quote có thể là chuỗi JSON hoặc object JSON
  try {
    const payload = JSON.parse(e.data); // { symbol, price, pctChange, ... }
    // TODO: cập nhật UI heatmap từ payload
  } catch {
    // nếu server gửi string JSON thô
    // hãy parse như trên; nếu là object đã serialize sẵn thì dùng trực tiếp
  }
});

es.onerror = () => {
  // Trình duyệt sẽ tự reconnect theo EventSource
};
```

Ví dụ theo dõi bằng curl:
```bash
curl -N "http://localhost:8080/stellar-api/v1/heatmap/stream"
```

Mẫu bản tin:
- ping
```
event: ping
data: {"ts":"2026-07-04T19:00:00Z"}
```

- quote (ví dụ)
```
event: quote
data: {"symbol":"FPT","price":123400,"pctChange":1.15,"volume":1000000}
```

Ghi chú:
- Khi mất mạng/kết nối, server có thể loại bỏ emitter và client sẽ tự reconnect.
- Hãy hiển thị dữ liệu từ snapshot trước, sau đó hợp nhất các bản tin quote để cập nhật theo thời gian thực.
- Backend sử dụng Redis làm nguồn dữ liệu và gửi heartbeat mỗi ~20 giây để giữ kết nối hoạt động.

---

## OHLCV APIs

### GET /stellar-api/v1/ohlcv/{symbol}
Returns historical OHLCV rows for one symbol. This endpoint is intended for candlestick charts, volume charts, and daily price charts. Current data is EOD/daily with `timeframe=1D`, while the `timeframe` parameter is kept for future expansion.

**Parameters:**
- `timeframe` *(optional)*: defaults to `1D`
- `fromDateSk` *(optional)*: inclusive start date in `YYYYMMDD`
- `toDateSk` *(optional)*: inclusive end date in `YYYYMMDD`
- `limit` *(optional)*: defaults to `500`, max `5000`
- `order` *(optional)*: defaults to `asc`; accepted values: `asc`, `desc`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/ohlcv/FPT?timeframe=1D&limit=500"
```

**Response (200):**
```json
[
  {
    "symbol": "FPT",
    "symbolSk": 123,
    "dateSk": 20260704,
    "fullDate": "2026-07-04",
    "timeframe": "1D",
    "timeSk": 0,
    "open": 121000,
    "high": 124000,
    "low": 120500,
    "close": 123400,
    "volume": 1000000,
    "value": 123400000000,
    "marketCap": 150000000000000,
    "marketWeight": 0.025
  }
]
```

**Status Codes:** `200 OK` | `400 Bad Request`

---

### GET /stellar-api/v1/ohlcv
Returns OHLCV rows for all symbols on one date. This endpoint supports market overview, heatmap fallback from database, and EOD price tables.

**Parameters:**
- `dateSk` *(required)*: date in `YYYYMMDD`
- `timeframe` *(optional)*: defaults to `1D`
- `limit` *(optional)*: defaults to `199`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/ohlcv?dateSk=20260704&timeframe=1D&limit=199"
```

**Status Codes:** `200 OK` | `400 Bad Request`

---

### GET /stellar-api/v1/ohlcv/latest
Returns OHLCV rows for all symbols on the latest available `date_sk` for the requested timeframe. The frontend does not need to know the latest trading date.

**Parameters:**
- `timeframe` *(optional)*: defaults to `1D`
- `limit` *(optional)*: defaults to `199`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/ohlcv/latest?timeframe=1D&limit=199"
```

**Status Codes:** `200 OK`

---

### GET /stellar-api/v1/symbols
Returns minimal symbol metadata for frontend symbol selectors. This API only uses `symbol_id`, `symbol`, `is_active`, `is_current`, `shares_outstanding`, and `freefloat` from `stellar_dm.dim_symbol`.

**Parameters:**
- `activeOnly` *(optional)*: defaults to `true`
- `limit` *(optional)*: defaults to `500`, max `2000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/symbols?activeOnly=true&limit=500"
```

**Response (200):**
```json
[
  {
    "symbolSk": 123,
    "symbol": "FPT",
    "isActive": true,
    "sharesOutstanding": 1500000000,
    "freefloat": 0.85
  }
]
```

**Status Codes:** `200 OK`

---

### GET /stellar-api/v1/market-structure
Latest market structure snapshot by timeframe.

**Parameters:**
- `timeframe` *(required)*: `1M`, `3M`, `6M`, `1Y`
- `benchmark` *(optional)*: defaults to `VNINDEX`

**Response (200):**
```json
{
  "dateSk": 20260423,
  "timeframe": "3M",
  "benchmark": "VNINDEX",
  "marketStructureCode": "MIXED",
  "marketStructureLabel": "Cấu trúc Phân Hóa (Mixed)",
  "coreSectors": [
    {
      "block_type": "KHỐI VẬN TẢI",
      "appearances": 58,
      "sector_code": "SHIPPING",
      "sector_name": "Vận tải biển/cảng",
      "avg_strength": 1.5808,
      "latest_strength": 1.0162
    },
    {
      "block_type": "KHỐI CÔNG NGHIỆP, TÀI NGUYÊN",
      "appearances": 57,
      "sector_code": "FERTILIZER_CHEMICAL",
      "sector_name": "Phân bón, hoá chất",
      "avg_strength": 2.0337,
      "latest_strength": 0.9757
    }
  ],
  "coreBlocks": [
    "KHỐI CÔNG NGHIỆP, TÀI NGUYÊN",
    "KHỐI TÀI CHÍNH",
    "KHỐI VẬN TẢI"
  ],
  "topEcosystemCode": "FPT_ECOSYSTEM",
  "topEcosystemName": "FPT",
  "sectorRankings": [
    {
      "block_type": "KHỐI VẬN TẢI",
      "appearances": 58,
      "sector_code": "SHIPPING",
      "sector_name": "Vận tải biển/cảng",
      "avg_strength": 1.5808,
      "latest_strength": 1.0162
    },
    {
      "block_type": "KHỐI CÔNG NGHIỆP, TÀI NGUYÊN",
      "appearances": 57,
      "sector_code": "FERTILIZER_CHEMICAL",
      "sector_name": "Phân bón, hoá chất",
      "avg_strength": 2.0337,
      "latest_strength": 0.9757
    }
  ],
  "ecosystemRankings": [
    {
      "block_type": null,
      "appearances": 57,
      "avg_strength": 2.0231,
      "ecosystem_code": "FPT_ECOSYSTEM",
      "ecosystem_name": "FPT",
      "latest_strength": 0.7903
    },
    {
      "block_type": null,
      "appearances": 1,
      "avg_strength": 1.1427,
      "ecosystem_code": "VINGROUP",
      "ecosystem_name": "Vingroup",
      "latest_strength": 1.1427
    }
  ],
  "ingestionTime": "2026-04-23T11:10:04.291543"
}
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---

#### Response Fields

| Field | Type | Mô tả |
|-------|------|-----------|
| `dateSk` | integer | Khóa ngày định dạng YYYYMMDD |
| `timeframe` | string | Khung thời gian: `1M`, `3M`, `6M`, `1Y` |
| `benchmark` | string | Chỉ số chuẩn (ví dụ: VNINDEX) |
| `marketStructureCode` | string | Mã xác định cấu trúc thị trường (ví dụ: MIXED, TREND) |
| `marketStructureLabel` | string | Tên cấu trúc thị trường dễ đọc |
| `coreSectors` | array | **Các ngành lõi** thúc đẩy thị trường - mảng các đối tượng ngành có chỉ số lực mạnh |
| `coreBlocks` | array | **Các khối kinh doanh lõi** - mảng tên các loại khối |
| `topEcosystemCode` | string | Mã của nhóm hệ sinh thái hàng đầu |
| `topEcosystemName` | string | Tên của nhóm hệ sinh thái hàng đầu |
| `sectorRankings` | array | **Tất cả ngành xếp hạng** - danh sách xếp hạng đầy đủ với chỉ số hiệu suất |
| `ecosystemRankings` | array | **Tất cả hệ sinh thái xếp hạng** - xếp hạng đầy đủ các nhóm hệ sinh thái |
| `ingestionTime` | string | Dấu thời gian ISO 8601 khi dữ liệu được nhập |

**⭐ Các field BẮT BUỘC để hiển thị Cấu trúc Thị trường:**
| Field | Mục đích | Ghi chú |
|-------|---------|--------|
| `marketStructureLabel` | Hiển thị **tiêu đề cấu trúc** | Ví dụ: "Cấu trúc Phân Hóa (Mixed)" |
| `coreSectors` | Hiển thị **ngành lõi chính** | Dùng: `sector_name`, `latest_strength` |
| `coreBlocks` | Hiển thị **các khối chiếm ưu thế** | Danh sách các khối kinh doanh |
| `topEcosystemName` | Hiển thị **hệ sinh thái hàng đầu** | Tên công ty/tập đoàn lớn |
| `sectorRankings` | Hiển thị **danh sách xếp hạng ngành** | Sắp xếp theo `latest_strength` giảm dần |

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
      "sectorCode": "AGRICULTURE",
      "dateSk": 20260423,
      "rs": 79.15,
      "rm": 98.68,
      "phase": "LAGGING",
      "stockCount": 8,
      "totalStocks": 8,
      "sectorName": "Nông Nghiệp",
      "sectorNameEn": "Agriculture",
      "blockType": "KHỐI BÁN LẺ/TIÊU DÙNG/XUẤT NHẬP KHẨU",
      "topStocksByCap": {
        "mid": [
          {"rm": 102.6931, "rs": 89.1084, "symbol": "BAF"},
          {"rm": 93.9914, "rs": 76.695, "symbol": "LTG"}
        ],
        "large": [
          {"rm": 98.9663, "rs": 66.7556, "symbol": "HAG"}
        ],
        "small": [
          {"rm": 110.4496, "rs": 108.2321, "symbol": "HNG"}
        ]
      },
      "benchmark": "VNINDEX",
      "ingestionTime": "2026-04-23T11:00:07.434856",
      "regime": "FLEXIBLE"
    },
    {
      "sectorCode": "BANKS",
      "dateSk": 20260423,
      "rs": 74.39,
      "rm": 97.29,
      "phase": "LAGGING",
      "stockCount": 16,
      "totalStocks": 16,
      "sectorName": "Ngân hàng",
      "sectorNameEn": "Banks",
      "blockType": "KHỐI TÀI CHÍNH",
      "topStocksByCap": {
        "mid": [
          {"rm": 81.6364, "rs": 78.2593, "symbol": "HDB"},
          {"rm": 108.709, "rs": 71.2577, "symbol": "STB"}
        ],
        "large": [
          {"rm": 144.0374, "rs": 113.713, "symbol": "VCB"},
          {"rm": 103.4329, "rs": 100.1779, "symbol": "TCB"}
        ],
        "small": [
          {"rm": 106.4164, "rs": 78.6099, "symbol": "MSB"}
        ]
      },
      "benchmark": "VNINDEX",
      "ingestionTime": "2026-04-23T11:00:07.434856",
      "regime": "FLEXIBLE",
      "totalVolume": 123456789,
      "totalValue": 987654321000,
      "totalMarketCap": 150000000000000,
      "avgMarketCap": 1200000000000,
      "liquidityScore": 0.82,
      "totalFreefloatMarketCap": 90000000000000,
      "avgMarketWeight": 0.035
    }
  ]
}
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---

#### Response Fields

| Field | Type | Mô tả |
|-------|------|-----------|
| `items` | array | **Mảng các mục RRG** |
| `sectorCode` | string | Mã định danh duy nhất của ngành |
| `sectorName` | string | Tên ngành bằng tiếng Việt |
| `sectorNameEn` | string | Tên ngành bằng tiếng Anh |
| `dateSk` | integer | Khóa ngày định dạng YYYYMMDD |
| `rs` | number | **Relative Strength** - hiệu suất so với chỉ số (thang 0-100) |
| `rm` | number | **Relative Momentum** - chỉ số động lực (thang 0-100) |
| `phase` | string | Pha RRG: `LEADING`, `WEAKENING`, `LAGGING`, `IMPROVING` |
| `blockType` | string | Loại khối/danh mục kinh doanh |
| `stockCount` | integer | Số lượng cổ phiếu có dữ liệu trong ngành |
| `totalStocks` | integer | Tổng số cổ phiếu trong ngành |
| `topStocksByCap` | object | **Cổ phiếu theo vốn hóa** - nhóm theo `small` (nhỏ), `mid` (vừa), `large` (lớn) hoặc `leading` |
| `- symbol` | string | Mã chứng chỉ cổ phiếu |
| `- rs` | number | Relative Strength của cổ phiếu |
| `- rm` | number | Relative Momentum của cổ phiếu |
| `benchmark` | string | Chỉ số chuẩn được sử dụng |
| `regime` | string | Chế độ thị trường: `VENTURE`, `FLEXIBLE`, `ENDURING` |
| `ingestionTime` | string | Dấu thời gian ISO 8601 khi dữ liệu được nhập |
| `totalVolume` | number | Tổng khối lượng giao dịch của sector; có thể dùng để filter/sort theo thanh khoản |
| `totalValue` | number | Tổng giá trị giao dịch của sector; có thể dùng để filter/sort sector |
| `totalMarketCap` | number | Tổng vốn hóa sector; có thể dùng để scale bubble size |
| `avgMarketCap` | number | Vốn hóa trung bình trong sector; có thể dùng để filter nhóm sector theo quy mô |
| `liquidityScore` | number | Điểm thanh khoản; có thể dùng để filter/sort sector |
| `totalFreefloatMarketCap` | number | Tổng freefloat market cap; có thể dùng thay `totalMarketCap` khi muốn scale theo freefloat |
| `avgMarketWeight` | number | Trọng số thị trường trung bình của sector |

**Ghi chú field mới:** `totalMarketCap` có thể dùng để scale bubble size. `liquidityScore`, `totalValue`, `totalVolume` có thể dùng để frontend filter hoặc sort sector. `totalFreefloatMarketCap` có thể dùng thay `totalMarketCap` khi muốn scale theo freefloat. Các field mới có thể `null` nếu cache chưa backfill đầy đủ.

**Giải thích Pha RRG:**
- 🚀 **LEADING**: Hiệu suất mạnh mẽ, giai đoạn tăng trưởng
- ⬆️ **IMPROVING**: Chuyển tiếp từ yếu sang mạnh
- ⬇️ **WEAKENING**: Chuyển tiếp từ mạnh sang yếu
- 📉 **LAGGING**: Hiệu suất yếu, giai đoạn suy giảm

**⭐ Các field BẮT BUỘC để vẽ RRG Chart:**
| Field | Mục đích | Ghi chú |
|-------|---------|--------|
| `rs` | Trục Y (Relative Strength) | Giá trị 0-100, vị trí dọc trên biểu đồ |
| `rm` | Trục X (Relative Momentum) | Giá trị 0-100, vị trí ngang trên biểu đồ |
| `sectorName` | Nhãn điểm | Hiển thị tên ngành khi hover |
| `phase` | Màu điểm | LEADING (xanh), IMPROVING (vàng), WEAKENING (cam), LAGGING (đỏ) |
| `blockType` | Phân loại | Dùng để nhóm hoặc lọc ngành |

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
    "sectorCode": "AGRICULTURE",
    "blockType": "KHỐI BÁN LẺ/TIÊU DÙNG/XUẤT NHẬP KHẨU",
    "timeframe": "1M",
    "chartData": [
      {"date": 20260326, "value": 0.0},
      {"date": 20260327, "value": -0.58},
      {"date": 20260330, "value": 1.02},
      {"date": 20260331, "value": 0.36},
      {"date": 20260401, "value": -1.04}
    ],
    "ingestionTime": "2026-04-24T15:21:05.052485"
  },
  {
    "sectorCode": "BANKS",
    "blockType": "KHỐI TÀI CHÍNH",
    "timeframe": "1M",
    "chartData": [
      {"date": 20260326, "value": 0.0},
      {"date": 20260327, "value": -0.29},
      {"date": 20260330, "value": -0.09},
      {"date": 20260331, "value": 0.3},
      {"date": 20260401, "value": -1.13}
    ],
    "ingestionTime": "2026-04-24T15:21:05.052485"
  }
]
```

**Status Codes:** `200 OK` | `204 No Content` | `400 Bad Request`

---

#### Response Fields

| Field | Type | Bắt buộc cho Biểu đồ | Mô tả |
|-------|------|:-:|-----------|
| `sectorCode` | string | ❌ | Mã định danh ngành (ví dụ: AGRICULTURE, BANKS, TECHNOLOGY) |
| `blockType` | string | ❌ | Loại khối/danh mục kinh doanh (ví dụ: KHỐI TÀI CHÍNH, KHỐI CÔNG NGHỆ) |
| `timeframe` | string | ❌ | Khung thời gian dữ liệu: `1M`, `3M`, `6M`, `1Y` |
| `chartData` | array | ✅ **CÓ** | **Mảng dữ liệu biểu đồ** - BẮT BUỘC cho hiển thị biểu đồ |
| `- date` | number | ✅ **CÓ** | Ngày định dạng YYYYMMDD (ví dụ: 20260326 = 26/03/2026) |
| `- value` | number | ✅ **CÓ** | Giá trị hiệu suất (thay đổi theo phần trăm) |
| `ingestionTime` | string | ❌ | Dấu thời gian ISO 8601 khi dữ liệu được nhập |

**Yêu cầu vẽ biểu đồ:**
- Chỉ cần mảng `chartData` - chứa các cặp ngày & giá trị
- Tối thiểu 1 điểm dữ liệu, thường 20-21 điểm cho khung thời gian hàng tháng
- Giá trị đầu tiên thường là 0.0 (điểm tham chiếu gốc)
- Các giá trị tiếp theo biểu thị thay đổi hiệu suất phần trăm tích lũy
- Phân tích trường `date` định dạng YYYYMMDD và chuyển thành ngày thực cho trục x

**⭐ Các field BẮT BUỘC để vẽ Performance Chart:**
| Field | Mục đích | Chi tiết |
|-------|---------|---------|
| `chartData[].date` | Trục X (Ngày) | Định dạng YYYYMMDD, chuyển đổi thành ngày D/M/Y |
| `chartData[].value` | Trục Y (Giá trị) | Phần trăm thay đổi hiệu suất, có thể âm hoặc dương |
| `sectorCode` | Nhãn biểu đồ | Hiển thị ở tiêu đề (ví dụ: "AGRICULTURE") |
| `sectorName` (nếu có) | Tiêu đề đầy đủ | Tên ngành tiếng Việt nếu API trả về |
| `blockType` | Phân loại màu | Dùng để phân biệt các nhóm ngành |
| `timeframe` | Khoảng thời gian | Hiển thị "1 Tháng", "3 Tháng", v.v. |

---
## Lưu ý cho Frontend khi sử dụng Heatmap Realtime SSE

### 1. Frontend phải gọi snapshot trước, mở SSE sau

Luồng xử lý đúng:

```text
GET /stellar-api/v1/heatmap/snapshot
→ vẽ heatmap ban đầu
→ mở EventSource /stellar-api/v1/heatmap/stream
→ nhận event quote để cập nhật từng mã
```

Không nên chờ SSE để dựng heatmap ban đầu.

### 2. Snapshot là nguồn dữ liệu đồng bộ, SSE là cập nhật realtime

- Snapshot trả trạng thái đầy đủ hiện tại.
- SSE chỉ đẩy cập nhật mới sau thời điểm client kết nối.
- SSE không đảm bảo phát lại các event đã bị bỏ lỡ nếu client mất kết nối.
- Redis Pub/Sub/SSE chỉ dùng để đẩy cập nhật thay đổi, không phải nguồn dữ liệu đầy đủ.

### 3. Khi reconnect hoặc lỗi SSE, frontend nên đồng bộ lại snapshot

- Trình duyệt `EventSource` có cơ chế tự reconnect.
- Tuy nhiên nếu mất kết nối lâu, frontend nên gọi lại:
  `GET /stellar-api/v1/heatmap/snapshot`
- Sau đó tiếp tục nhận cập nhật qua SSE.

### 4. Định dạng sự kiện

Luồng SSE:

```js
const eventSource = new EventSource("/stellar-api/v1/heatmap/stream");

eventSource.addEventListener("quote", (event) => {
  const quote = JSON.parse(event.data);

  // Cập nhật theo symbol, không thay toàn bộ heatmap nếu không cần thiết
  quotesBySymbol[quote.symbol] = {
    ...quotesBySymbol[quote.symbol],
    ...quote,
  };

  renderHeatmap();
});

eventSource.addEventListener("ping", () => {
  // Event heartbeat. Không cần cập nhật UI.
});

eventSource.onerror = () => {
  // Trình duyệt sẽ tự thử kết nối lại.
  // Nếu mất kết nối lâu, tải lại snapshot để tránh hiển thị dữ liệu cũ.
};
```

Tên event:

- `quote`: cập nhật realtime cho từng quote.
- `ping`: event heartbeat, dùng để giữ kết nối và kiểm tra trạng thái kết nối.

### 5. Cập nhật theo symbol, không thay thế toàn bộ danh sách

- Nên giữ state dạng map/object theo symbol.
- Ví dụ: `quotesBySymbol["FPT"] = quote`
- Khi nhận event mới, chỉ cập nhật mã tương ứng.
- Không nên dựng lại toàn bộ heatmap nếu không cần thiết.

### 6. Dữ liệu cần dùng cho heatmap

Frontend nên dùng:

- `symbol`: định danh ô
- `pctChange`: màu ô
- `marketCap`: kích thước ô
- `price`, `volume`, `txnValue`: tooltip
- `status`: trạng thái tăng/giảm/tham chiếu
- `timestamp` hoặc `lastUpdated`: thời điểm cập nhật

### 7. Lưu ý về reconnect

- Khi trang vừa mở: luôn gọi snapshot.
- Khi SSE reconnect sau lỗi: nên chống gọi lặp việc tải lại snapshot.
- Ví dụ chỉ tải lại snapshot nếu mất kết nối trên 5-10 giây.
- Không mở nhiều `EventSource` trùng nhau cho cùng một màn hình.
- Khi component bị gỡ khỏi màn hình hoặc người dùng rời trang, phải gọi:
  `eventSource.close()`

