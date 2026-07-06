# API Reference

## Stellar API - `/stellar-api/v1`

## Authentication

All endpoints under `/stellar-api/v1/**` require a JWT Bearer Token.

REST API header:
```http
Authorization: Bearer <JWT_TOKEN>
```

Example:
```bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
https://api.staging-stellar.io.vn/stellar-api/v1/ohlcv/latest
```

SSE browser usage:
```text
/stellar-api/v1/heatmap/stream?token=<JWT_TOKEN>
```

Browser `EventSource` does not support custom `Authorization` headers, so the SSE stream endpoint also accepts the JWT in the `token` query parameter. This query-param token fallback is only supported for `/stellar-api/v1/heatmap/stream`.

If the token is missing or invalid, the API returns:
```http
401 Unauthorized
```

## Tổng hợp API hiện có
- Foreign Flow
  - GET /stellar-api/v1/foreign-flow/chart — Dữ liệu chuỗi thời gian theo mã cổ phiếu/ngành/thị trường
  - GET /stellar-api/v1/foreign-flow/heatmap — Dữ liệu heatmap theo ngày và timeframe
- Heatmap (Realtime qua SSE)
  - GET /stellar-api/v1/heatmap/snapshot — Ảnh chụp heatmap hiện tại (từ Redis)
  - GET /stellar-api/v1/heatmap/stream — Luồng sự kiện Server-Sent Events (SSE) cập nhật realtime
- Market Breadth
  - GET /stellar-api/v1/market/breadth — Độ rộng thị trường hiện tại từ Redis
  - GET /stellar-api/v1/market/breadth/history — Độ rộng thị trường theo ngày
- OHLCV
  - GET /stellar-api/v1/ohlcv/{symbol} — Lịch sử OHLCV theo mã
  - GET /stellar-api/v1/ohlcv — OHLCV cho tất cả mã trong một ngày
  - GET /stellar-api/v1/ohlcv/latest — OHLCV mới nhất cho tất cả mã
- Index OHLCV
  - GET /stellar-api/v1/index-ohlcv/{symbol} - Index OHLCV history by symbol
  - GET /stellar-api/v1/index-ohlcv - Index OHLCV rows by date
  - GET /stellar-api/v1/index-ohlcv/latest - Latest index OHLCV rows
- Anomalies
  - GET /stellar-api/v1/anomalies — Danh sách anomaly và kết quả AI analysis
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
- `entityType` *(required)*: examples `STOCK`, `MARKET`
- `entityCode` *(required)*: examples `FPT`, `BANK`, `VNINDEX`
- `timeframe` *(required)*: examples `1M`, `3M`, `6M`, `1Y`
- `fromDateSk` *(optional)*: inclusive start date in `YYYYMMDD`
- `toDateSk` *(optional)*: inclusive end date in `YYYYMMDD`
- `limit` *(optional)*: defaults to `500`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/foreign-flow/chart?entityType=STOCK&entityCode=FPT&timeframe=1D&limit=200"
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
    "close": 123400,
    "priceIndex100": 108.5,
    "benchmarkCode": "VNINDEX",
    "benchmarkClose": 1280.5,
    "benchmarkIndex100": 103.2,
    "ingestionTime": "2026-07-04T17:30:00"
  }
]
```

**Response fields and chart usage:**

| Field | Meaning | Chart usage |
|-------|---------|-------------|
| `entityType` | Loai entity cua chuoi du lieu, vi du `SYMBOL`, `SECTOR`, `MARKET`. | Context/filter label. |
| `entityCode` | Ma entity, vi du `FPT`, `BANK`, `VNINDEX`. | Series label. |
| `dateSk` | Ngay du lieu dang `YYYYMMDD`. | **Required** for X-axis. |
| `timeframe` | Khung thoi gian cua chuoi du lieu. | Context label/filter. |
| `buyVal` | Gia tri mua cua khoi ngoai. | **Required** for buy/sell bar or stacked chart. |
| `sellVal` | Gia tri ban cua khoi ngoai. | **Required** for buy/sell bar or stacked chart. |
| `netVal` | Gia tri mua rong: buy minus sell. | **Required** for net-flow line/bar chart. |
| `cumulativeNetVal` | Luy ke mua rong den ngay hien tai trong timeframe. | **Required** for cumulative flow line chart. |
| `close` | Gia dong cua entity neu co. | Price overlay or tooltip. |
| `priceIndex100` | Gia entity quy ve moc 100. | **Required** for relative performance chart. |
| `benchmarkCode` | Ma benchmark dung de so sanh. | Legend/context. |
| `benchmarkClose` | Gia dong cua benchmark. | Tooltip/context. |
| `benchmarkIndex100` | Benchmark quy ve moc 100. | **Required** for relative performance chart. |
| `ingestionTime` | Thoi diem cache/ETL ghi du lieu. | Debug/staleness indicator. |

Important chart fields: `dateSk`, `netVal`, `cumulativeNetVal`, `buyVal`, `sellVal`, `priceIndex100`, `benchmarkIndex100`.

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
    "dateSk": 20260703,
    "timeframe": "1M",
    "symbol": "HPG",
    "symbolSk": 76,
    "netVal": -40537096600.0,
    "cumulativeNetVal": -357382926800.0,
    "close": 23250.0,
    "pctChange": -0.6410256410256387,
    "volume": 15963512,
    "value": 372335129700,
    "marketCap": 196298925090000,
    "marketWeight": 0.0216431577863547,
    "rankNetBuy": 1529,
    "rankNetSell": 4,
    "intensity": 0.1702643478391268,
    "direction": "SELL",
    "ingestionTime": "2026-07-03T10:39:48.968762"
  }
]
```

**Response fields and heatmap usage:**

| Field | Meaning | Heatmap/chart usage |
|-------|---------|---------------------|
| `dateSk` | Ngay du lieu dang `YYYYMMDD`. | Context/filter label. |
| `timeframe` | Khung thoi gian aggregate, vi du `1M`. | Context/filter label. |
| `symbol` | Ma co phieu/index. | **Required** as tile id and label. |
| `symbolSk` | Surrogate key cua symbol trong data mart. | Internal id, useful for drill-down. |
| `netVal` | Gia tri mua rong cua khoi ngoai trong ky. | **Required** for color/tooltip; positive is net buy, negative is net sell. |
| `cumulativeNetVal` | Luy ke mua rong trong timeframe. | **Required** for stronger signal/ranking tooltip. |
| `netVol` | Khoi luong mua rong neu ETL co data. | Optional; ignore when null. |
| `cumulativeNetVol` | Luy ke khoi luong mua rong neu ETL co data. | Optional; ignore when null. |
| `close` | Gia dong/last price tai ngay snapshot. | Tooltip and detail panel. |
| `pctChange` | % thay doi gia. | Secondary color/tooltip for price move. |
| `volume` | Khoi luong khop lenh. | Tooltip/liquidity context. |
| `value` | Gia tri giao dich. | Tooltip/liquidity context. |
| `marketCap` | Von hoa thi truong. | **Recommended** for tile size. |
| `marketWeight` | Ty trong von hoa/thi truong. | **Recommended** for tile size or weighting. |
| `rankNetBuy` | Thu hang mua rong; so nho hon la mua rong manh hon. | Sort/filter top net-buy. |
| `rankNetSell` | Thu hang ban rong; so nho hon la ban rong manh hon. | Sort/filter top net-sell. |
| `intensity` | Do manh tin hieu da normalize. | **Required** for heatmap color opacity/intensity. |
| `direction` | Huong dong tien: `BUY`, `SELL`, `NEUTRAL`. | **Required** for color palette. |
| `ingestionTime` | Thoi diem cache/ETL ghi du lieu. | Debug/staleness indicator. |

Important heatmap fields: `symbol`, `direction`, `intensity`, `netVal`, `cumulativeNetVal`, `marketCap` or `marketWeight`.

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

**Response fields and realtime heatmap usage:**

| Field | Meaning | Heatmap usage |
|-------|---------|---------------|
| `symbol` | Mã cổ phiếu. | **Required** as tile id and update key. |
| `price` | Giá khớp/last price hiện tại. | Tile label or tooltip. |
| `refPrice` | Giá tham chiếu. | Tooltip and price-change calculation fallback. |
| `pctChange` | % thay đổi so với tham chiếu. | **Required** for price heatmap color. |
| `volume` | Khối lượng giao dịch. | Tooltip/liquidity context. |
| `txnValue` | Giá trị giao dịch. | Tooltip/liquidity context. |
| `marketCap` | Vốn hóa. | **Recommended** for tile size. |
| `status` | Trạng thái giao dịch, ví dụ `TRADING`, `HALTED`. | Style disabled/paused tiles. |
| `sector` | Ngành/cụm sector. | Grouping/filter. |
| `industry` | Ngành chi tiết. | Tooltip/filter. |
| `exchange` | Sàn giao dịch. | Filter/grouping. |
| `lastUpdated` | Thời điểm update ISO-8601. | Staleness indicator. |

Important realtime heatmap fields: `symbol`, `pctChange`, `marketCap`, `price`, `volume`, `txnValue`, `lastUpdated`.

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

## Market Breadth APIs

### GET /stellar-api/v1/market/breadth
Returns current market breadth from Redis.

### GET /stellar-api/v1/market/breadth/history
Returns market breadth history for a date.

**Parameters:**
- `date` *(optional)*: date key, usually `YYYYMMDD`; defaults to today for history if omitted.

**Typical response fields:**

| Field | Meaning | Chart usage |
|-------|---------|-------------|
| `advance` / `advances` | Number of advancing stocks. | **Required** for breadth stacked bar/donut. |
| `decline` / `declines` | Number of declining stocks. | **Required** for breadth stacked bar/donut. |
| `unchanged` / `noChange` / `no_change` | Number of unchanged stocks. | **Required** for neutral segment. |
| `total` / `totalStocks` / `total_stocks` | Total counted stocks. | Denominator for percentages. |
| `advancePct` | Advancing percentage, added by backend for history map responses. | **Required** for percentage breadth chart. |
| `declinePct` | Declining percentage, added by backend for history map responses. | **Required** for percentage breadth chart. |
| `unchangedPct` | Unchanged percentage, added by backend for history map responses. | Neutral percentage segment. |
| Other Redis fields | Extra breadth dimensions from upstream feed. | Optional tooltip/context. |

Important breadth fields: `advance`, `decline`, `unchanged`, `total`, `advancePct`, `declinePct`.

**Status Codes:** `200 OK` | `204 No Content`

---

## Anomaly APIs

### GET /stellar-api/v1/anomalies
Returns anomaly rows and AI analysis labels.

**Response (200):**
```json
[
  {
    "symbol": "FPT",
    "date": "2026-07-04",
    "ai_analysis": {
      "anomaly_score": 0.92,
      "is_anomaly": true,
      "status_label": "HIGH_RISK"
    },
    "explanation": "Unusual price/volume movement detected."
  }
]
```

**Response fields and usage:**

| Field | Meaning | UI/chart usage |
|-------|---------|----------------|
| `symbol` | Mã cổ phiếu. | **Required** for table row and drill-down. |
| `date` | Ngày phát hiện anomaly. | **Required** for timeline/table sorting. |
| `ai_analysis.anomaly_score` | Điểm bất thường, thường dùng thang 0-1. | **Required** for severity color/score chart. |
| `ai_analysis.is_anomaly` | Cờ xác nhận có bất thường. | Filter/highlight rows. |
| `ai_analysis.status_label` | Nhãn trạng thái từ model. | Badge/severity label. |
| `explanation` | Giải thích ngắn về anomaly. | Tooltip/detail panel. |

Important anomaly fields: `symbol`, `date`, `ai_analysis.anomaly_score`, `ai_analysis.is_anomaly`, `ai_analysis.status_label`.

**Status Codes:** `200 OK`

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

**Response fields and chart usage:**

| Field | Meaning | Chart usage |
|-------|---------|-------------|
| `symbol` | Mã cổ phiếu. | Series label and route key. |
| `symbolSk` | Surrogate key của symbol trong data mart. | Internal id/drill-down. |
| `dateSk` | Ngày dạng `YYYYMMDD`. | **Required** for X-axis if not using `fullDate`. |
| `fullDate` | Ngày ISO `YYYY-MM-DD`. | **Required** for X-axis in UI. |
| `timeframe` | Khung thời gian, hiện tại dữ liệu EOD dùng `1d`. | Context/filter label. |
| `timeSk` | Khóa thời gian trong ngày; EOD thường là `0`. | Intraday extension/context. |
| `open` | Giá mở cửa. | **Required** for candlestick/OHLC chart. |
| `high` | Giá cao nhất. | **Required** for candlestick/OHLC chart. |
| `low` | Giá thấp nhất. | **Required** for candlestick/OHLC chart. |
| `close` | Giá đóng cửa. | **Required** for candlestick/line chart and return calculation. |
| `volume` | Khối lượng giao dịch. | **Required** for volume histogram. |
| `value` | Giá trị giao dịch. | Liquidity tooltip/table. |
| `marketCap` | Vốn hóa. | Market heatmap tile size / ranking. |
| `marketWeight` | Tỷ trọng thị trường. | Market heatmap weighting / ranking. |

Important OHLCV chart fields: `fullDate` or `dateSk`, `open`, `high`, `low`, `close`, `volume`.

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

Response schema is the same as `GET /stellar-api/v1/ohlcv/{symbol}`. For market overview/heatmap, prioritize `symbol`, `close`, `volume`, `value`, `marketCap`, `marketWeight`.

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

Response schema is the same as `GET /stellar-api/v1/ohlcv/{symbol}`. Use this endpoint when the frontend needs the latest available trading date automatically.

---

## Index OHLCV APIs

### GET /stellar-api/v1/index-ohlcv/{symbol}
Returns historical OHLCV rows for one index symbol, for example `VNINDEX`, `HNXINDEX`, or `UPCOMINDEX`.

**Parameters:**
- `timeframe` *(optional)*: defaults to `1d`
- `fromDateSk` *(optional)*: inclusive start date in `YYYYMMDD`
- `toDateSk` *(optional)*: inclusive end date in `YYYYMMDD`
- `limit` *(optional)*: defaults to `500`, max `5000`
- `order` *(optional)*: defaults to `asc`; accepted values: `asc`, `desc`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/index-ohlcv/VNINDEX?timeframe=1d&limit=500"
```

**Response (200):**
```json
[
  {
    "symbol": "VNINDEX",
    "symbolSk": 180,
    "dateSk": 20260704,
    "fullDate": "2026-07-04",
    "timeframe": "1d",
    "timeSk": 0,
    "open": 1280.5,
    "high": 1294.2,
    "low": 1278.1,
    "close": 1290.4,
    "volume": 123456789,
    "value": 4567890000000
  }
]
```

**Response fields and chart usage:**

| Field | Meaning | Chart usage |
|-------|---------|-------------|
| `symbol` | Index code. | Series label and query key. |
| `symbolSk` | Surrogate key of the index symbol in data mart. | Internal id/drill-down. |
| `dateSk` | Date key in `YYYYMMDD`. | **Required** for X-axis if not using `fullDate`. |
| `fullDate` | ISO date `YYYY-MM-DD`. | **Required** for X-axis in UI. |
| `timeframe` | Data timeframe, currently usually `1d`. | Context/filter label. |
| `timeSk` | Time key; EOD usually uses `0`. | Intraday extension/context. |
| `open` | Opening index value. | **Required** for candlestick/OHLC chart. |
| `high` | Highest index value. | **Required** for candlestick/OHLC chart. |
| `low` | Lowest index value. | **Required** for candlestick/OHLC chart. |
| `close` | Closing index value. | **Required** for candlestick/line chart and return calculation. |
| `volume` | Matched volume for the index universe if available. | Volume histogram / tooltip. |
| `value` | Trading value for the index universe if available. | Liquidity chart / tooltip. |

Important index chart fields: `fullDate` or `dateSk`, `open`, `high`, `low`, `close`, `volume`, `value`.

**Status Codes:** `200 OK` | `400 Bad Request`

---

### GET /stellar-api/v1/index-ohlcv
Returns index OHLCV rows for all index symbols on one date.

**Parameters:**
- `dateSk` *(required)*: date in `YYYYMMDD`
- `timeframe` *(optional)*: defaults to `1d`
- `limit` *(optional)*: defaults to `100`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/index-ohlcv?dateSk=20260704&timeframe=1d&limit=100"
```

**Status Codes:** `200 OK` | `400 Bad Request`

Response schema is the same as `GET /stellar-api/v1/index-ohlcv/{symbol}`. For index overview charts, prioritize `symbol`, `close`, `volume`, and `value`.

---

### GET /stellar-api/v1/index-ohlcv/latest
Returns index OHLCV rows for all index symbols on the latest available `date_sk` for the requested timeframe.

**Parameters:**
- `timeframe` *(optional)*: defaults to `1d`
- `limit` *(optional)*: defaults to `100`, max `1000`

**Example:**
```bash
curl "http://localhost:8080/stellar-api/v1/index-ohlcv/latest?timeframe=1d&limit=100"
```

**Status Codes:** `200 OK`

Response schema is the same as `GET /stellar-api/v1/index-ohlcv/{symbol}`.

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

**Response fields and usage:**

| Field | Meaning | Usage |
|-------|---------|-------|
| `symbolSk` | Surrogate key của symbol trong data mart. | Internal id/drill-down. |
| `symbol` | Mã cổ phiếu. | **Required** for selectors, routing, chart query key. |
| `isActive` | Symbol còn active hay không. | Filter inactive symbols. |
| `sharesOutstanding` | Số cổ phiếu lưu hành. | Market-cap fallback / fundamentals tooltip. |
| `freefloat` | Tỷ lệ free-float. | Liquidity/freefloat weighting if needed. |

Important selector fields: `symbol`, `symbolSk`, `isActive`.

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

**Chart/display priority:**
- Market-structure summary card: `marketStructureCode`, `marketStructureLabel`, `dateSk`, `timeframe`, `benchmark`.
- Core sector/block view: `coreSectors`, `coreBlocks`.
- Ranking tables/charts: `sectorRankings`, `ecosystemRankings`.
- Tooltip/context: `topEcosystemCode`, `topEcosystemName`, `ingestionTime`.

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

**Chart/display priority:**
- RRG scatter position: `rm` as X-axis, `rs` as Y-axis.
- Point color: `phase`.
- Point label/tooltip: `sectorCode`, `sectorName`, `sectorNameEn`, `blockType`.
- Bubble size or ranking: `totalMarketCap`, `totalFreefloatMarketCap`, `avgMarketWeight`, `liquidityScore`.
- Drill-down tooltip: `topStocksByCap`, `stockCount`, `totalStocks`, `totalValue`, `totalVolume`.

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

**Chart/display priority:**
- Line chart: `chartData[].date`, `chartData[].value`.
- Series label: `sectorCode`.
- Grouping/color: `blockType`.
- Context: `timeframe`, `ingestionTime`.

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

