# API Reference

This document describes the active Spring Boot endpoints implemented by the repository. The default local base URL is:

~~~text
http://localhost:8080
~~~

The default Stellar API prefix is:

~~~text
/stellar-api/v1
~~~

There is no generated Swagger or OpenAPI endpoint in this application.

## Authentication and common behavior

All endpoints under the default Stellar API prefix require a JWT bearer token when STELLAR_JWT_ENABLED is true (the default).

~~~http
Authorization: Bearer <JWT_TOKEN>
~~~

The token must be signed with the configured HMAC secret and contain the configured issuer and audience claims. Defaults are:

| Claim/configuration | Default |
| --- | --- |
| issuer | stellar-api |
| audience | stellar-backend |
| JWT enabled | true |

If authentication fails, the service returns:

~~~http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{"error":"Unauthorized","message":"Missing or invalid JWT token"}
~~~

For browser Server-Sent Events only, the heatmap stream accepts the token query parameter because EventSource cannot set an Authorization header:

~~~text
/stellar-api/v1/heatmap/stream?token=<JWT_TOKEN>
~~~

Do not use a query token for other endpoints. It is not supported. Do not disable JWT outside a controlled local environment.

Every response includes an X-Correlation-ID header. Use it when correlating a client request with application logs.

### Response conventions

- JSON is the default response format. Dates use ISO-8601 strings; dateSk uses the numeric YYYYMMDD form.
- A 200 response with an empty array means the request was valid but found no rows.
- 204 No Content means the endpoint has no current object or no applicable rows. Its body is empty.
- A 400 response means the request could not be bound or passed endpoint validation. Several controller-generated 400 responses intentionally have an empty body.
- A 401 response can occur on every Stellar endpoint while JWT is enabled.
- Limit values less than 1 are treated as the endpoint default, not as an error.

The controller mappings are configurable through API_STELLAR_BASE_PATH, but the JWT filter and SSE token fallback are currently fixed to the default /stellar-api/v1 path. Keep the default path when JWT protection is enabled.

## Health

### GET /actuator/health

Unprotected health endpoint. It is the only exposed Actuator endpoint.

~~~bash
curl http://localhost:8080/actuator/health
~~~

Returns Spring Boot health information for the running application. Health details are enabled in configuration, so protect this endpoint at the deployment edge when necessary.

## Endpoint index

| Method | Path | Description |
| --- | --- | --- |
| GET | /foreign-flow/chart | Foreign-flow time series for an entity |
| GET | /foreign-flow/heatmap | Foreign-flow snapshot by date and timeframe |
| GET | /heatmap/snapshot | Current Redis-backed quote snapshot |
| GET | /heatmap/stream | Realtime quote stream using SSE |
| GET | /market/breadth | Current Redis-backed market breadth |
| GET | /market/breadth/history | Historical Redis-backed market breadth |
| GET | /stock-anomalies | Stock anomaly scores for a prediction date |
| GET | /ohlcv/{symbol} | OHLCV history for one symbol |
| GET | /ohlcv | OHLCV market snapshot for one date |
| GET | /ohlcv/latest | Latest OHLCV market snapshot |
| GET | /stocks/weights | Stock market-weight snapshot or range |
| GET | /index-impact/{indexCode}/latest | Current constituent-weight and index-impact snapshot |
| GET | /index-impact/{indexCode}/history | Intraday index-impact timeline from Redis Stream |
| GET | /index-valuation/{symbol}/historical | Historical index valuation data |
| GET | /index-valuation/{symbol}/latest | Latest index valuation data |
| GET | /index-ohlcv/{symbol} | Index OHLCV history for one symbol |
| GET | /index-ohlcv | Index OHLCV snapshot for one date |
| GET | /index-ohlcv/latest | Latest index OHLCV snapshot |
| GET | /symbols | Symbol metadata |
| GET | /symbols/available | Symbols that have OHLCV data |
| GET | /market-structure | Latest market-structure cache row |
| GET | /rrg | Sector relative-rotation graph data |
| GET | /sector-performance | Sector performance chart data |

All paths in the table are relative to /stellar-api/v1. The application exposes /stock-anomalies, not a legacy /anomalies route.

## Foreign flow

### GET /foreign-flow/chart

Returns a time series from the foreign-flow chart cache. Results are ordered by dateSk ascending.

| Query parameter | Required | Description |
| --- | --- | --- |
| entityType | Yes | Entity category, normalized to uppercase; it must match cached data |
| entityCode | Yes | Entity identifier, normalized to uppercase; for example FPT or VNINDEX |
| timeframe | Yes | Cached timeframe, normalized to uppercase; for example 1D or 1M |
| fromDateSk | No | Inclusive start date in YYYYMMDD |
| toDateSk | No | Inclusive end date in YYYYMMDD |
| limit | No | Default 500, maximum 1000 |

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/foreign-flow/chart?entityType=STOCK&entityCode=FPT&timeframe=1D&limit=200"
~~~

~~~json
[
  {
    "entityType": "STOCK",
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
~~~

Fields are omitted when the cached value is null. A missing required parameter returns 400.

### GET /foreign-flow/heatmap

Returns a foreign-flow heatmap snapshot. BUY results are sorted by cumulativeNetVal descending; SELL results are sorted ascending. Unfiltered and NEUTRAL results are sorted by the absolute value of cumulativeNetVal.

| Query parameter | Required | Description |
| --- | --- | --- |
| dateSk | Yes | Snapshot date in YYYYMMDD |
| timeframe | Yes | Cached timeframe, normalized to uppercase |
| direction | No | BUY, SELL, or NEUTRAL |
| limit | No | Default 199, maximum 1000 |

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/foreign-flow/heatmap?dateSk=20260704&timeframe=1D&direction=BUY"
~~~

~~~json
[
  {
    "dateSk": 20260704,
    "timeframe": "1D",
    "symbol": "FPT",
    "symbolSk": 76,
    "netVal": 3000000000,
    "cumulativeNetVal": 15000000000,
    "netVol": 200000,
    "cumulativeNetVol": 900000,
    "close": 123400,
    "pctChange": 1.2,
    "volume": 15963512,
    "value": 1960000000000,
    "marketCap": 210000000000000,
    "marketWeight": 0.031,
    "rankNetBuy": 1,
    "rankNetSell": 150,
    "intensity": 0.91,
    "direction": "BUY",
    "ingestionTime": "2026-07-04T17:30:00"
  }
]
~~~

## Realtime market data

### GET /heatmap/snapshot

Reads current quote records from Redis and returns an array. It returns 200 with an empty array when no quote keys exist.

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/stellar-api/v1/heatmap/snapshot
~~~

Each item has this shape:

| Field | Type | Meaning |
| --- | --- | --- |
| symbol | string | Ticker or quote identifier |
| price | number | Last price |
| refPrice | number | Reference price |
| pctChange | number | Percentage price change |
| volume | integer | Trading volume |
| txnValue | number | Trading value |
| marketCap | number | Market capitalization |
| status | string | Source-provided quote status |
| sector, industry, exchange | string | Source-provided metadata |
| lastUpdated | string | Source-provided update timestamp |

### GET /heatmap/stream

Opens a text/event-stream connection. The server emits an initial ping event, then a ping every 20 seconds. Quote updates arrive as quote events. A connection has a fixed 30-minute server-side timeout.

For command-line clients, use the bearer header:

~~~bash
curl -N -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/stellar-api/v1/heatmap/stream
~~~

For a browser EventSource, use the supported query-token fallback:

~~~javascript
const token = "<JWT_TOKEN>";
const stream = new EventSource(
  "/stellar-api/v1/heatmap/stream?token=" + encodeURIComponent(token)
);

stream.addEventListener("quote", (event) => {
  const quote = JSON.parse(event.data);
  // Merge this individual symbol into the current heatmap state.
});

stream.addEventListener("ping", (event) => {
  // Heartbeat data contains a timestamp.
});

stream.onerror = () => {
  // EventSource retries automatically. Refresh the snapshot after a long disconnect.
};
~~~

Recommended client flow:

1. Load /heatmap/snapshot to establish the full current state.
2. Open the stream.
3. Merge each quote event by symbol.
4. Reload the snapshot after a long disconnect or when re-establishing state.

The SSE stream is a change feed, not a replayable source of truth. Close the EventSource when the screen is no longer active.

### GET /market/breadth

Returns the current Redis-backed market breadth object, or 204 if no object exists for the current Asia/Ho_Chi_Minh date.

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/stellar-api/v1/market/breadth
~~~

When the source is a map, the service adds advancePct, declinePct, and unchangedPct. The remaining fields are source-defined.

### GET /market/breadth/history

Returns a historical Redis-backed breadth object or list. The optional date selects the Redis date key; omit it to use the current Asia/Ho_Chi_Minh date.

| Query parameter | Required | Description |
| --- | --- | --- |
| date | No | Expected date-key value, normally YYYYMMDD; it is not format-validated |

The service preserves the source Redis object shape and enriches map records with advancePct, declinePct, and unchangedPct. It returns 204 when no object exists.

## Stock anomalies

### GET /stock-anomalies

Returns anomaly records for a prediction date. Without date, it uses the latest available prediction date.

| Query parameter | Required | Description |
| --- | --- | --- |
| date | No | ISO date in YYYY-MM-DD |

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/stock-anomalies?date=2026-07-04"
~~~

~~~json
[
  {
    "id": 42,
    "symbol": "FPT",
    "sectorGroup": "TECHNOLOGY",
    "groupName": "Technology",
    "predictionDate": "2026-07-04",
    "score": 0.88,
    "p95": 0.70,
    "p98": 0.82,
    "scoreOverP95": 0.18,
    "scoreOverP98": 0.06,
    "baselineScore": 0.43,
    "baselineWindows": 20,
    "scoreRatioVsBaseline": 2.05,
    "anomalyCode": 2,
    "anomalyLevel": "HIGH",
    "relativeLevel": "ABOVE_P98",
    "finalDecision": "ALERT"
  }
]
~~~

## Stock OHLCV and weights

### GET /ohlcv/{symbol}

Returns OHLCV history for a symbol. The symbol is normalized to uppercase. Results are ordered by dateSk and timeSk; use order=desc to reverse the result.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | No | Stored timeframe; default 1d |
| fromDateSk | No | Inclusive start date in YYYYMMDD |
| toDateSk | No | Inclusive end date in YYYYMMDD |
| limit | No | Default 500, maximum 5000 |
| order | No | asc or desc; default asc |

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/ohlcv/FPT?timeframe=1d&fromDateSk=20260701&order=asc"
~~~

### GET /ohlcv

Returns the market snapshot for one date, sorted by marketCap descending.

| Query parameter | Required | Description |
| --- | --- | --- |
| dateSk | Yes | Snapshot date in YYYYMMDD |
| timeframe | No | Stored timeframe; default 1d |
| limit | No | Default 199, maximum 1000 |

### GET /ohlcv/latest

Uses the latest available date for the selected timeframe and returns a market snapshot sorted by marketCap descending.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | No | Stored timeframe; default 1d |
| limit | No | Default 199, maximum 1000 |

All three OHLCV endpoints return objects with these fields:

~~~json
{
  "symbol": "FPT",
  "symbolSk": 76,
  "dateSk": 20260704,
  "fullDate": "2026-07-04",
  "timeframe": "1d",
  "timeSk": 0,
  "open": 122000,
  "high": 124000,
  "low": 121500,
  "close": 123400,
  "volume": 15963512,
  "value": 1960000000000,
  "marketCap": 210000000000000,
  "marketWeight": 0.031
}
~~~

### GET /stocks/weights

Returns stock market-weight data from the stock OHLCV table.

| Query parameter | Required | Description |
| --- | --- | --- |
| symbol | No | Ticker filter, normalized to uppercase |
| sector | No | Sector filter, normalized to uppercase |
| dateSk | No | Snapshot date in YYYYMMDD |
| fromDateSk | No | Inclusive range start in YYYYMMDD |
| toDateSk | No | Inclusive range end in YYYYMMDD |
| timeframe | No | Stored timeframe; default 1d |
| limit | No | Default 1000, maximum 5000 |

When dateSk is absent and either range parameter is supplied, the request is a range query. When dateSk is present, it takes precedence and the range is ignored. Without dateSk or a range, the service uses the latest available date. A range whose start is later than its end returns 400.

Snapshot results are sorted by marketWeight descending, then symbol ascending. Range results are sorted by dateSk descending, then marketWeight descending and symbol ascending.

The endpoint returns 200 (including an empty array) or 400 when fromDateSk is later than toDateSk.

~~~json
{
  "symbol": "FPT",
  "symbolSk": 76,
  "companyName": "FPT Corporation",
  "sector": "TECHNOLOGY",
  "dateSk": 20260704,
  "fullDate": "2026-07-04",
  "timeframe": "1d",
  "timeSk": 0,
  "close": 123400,
  "volume": 15963512,
  "value": 1960000000000,
  "marketCap": 210000000000000,
  "marketWeight": 0.031
}
~~~

## Index data

### GET /index-impact/{indexCode}/latest

Returns the latest index-impact calculation stored in the Redis JSON key index:impact:{indexCode}:latest. The path indexCode is normalized to uppercase and accepts letters, digits, dots, underscores, and hyphens.

Returns 200 when a snapshot exists, 204 when the key is absent or blank, and 400 for an invalid indexCode.

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/stellar-api/v1/index-impact/VN30/latest
~~~

~~~json
{
  "index_code": "VN30",
  "timestamp": "2026-08-08T09:30:00+07:00",
  "index_value": 1450.5,
  "valid_count": 28,
  "missing_count": 2,
  "missing_symbols": ["ABC", "XYZ"],
  "missing_reasons": {
    "ABC": "missing quote",
    "XYZ": "stale price"
  },
  "total_adjusted_cap": 123456.78,
  "total_impact_point": 4.2,
  "top_positive": [
    {"symbol": "FPT", "impact_point": 1.2, "weight": 0.08, "pct_change": 1.5}
  ],
  "top_negative": [
    {"symbol": "VIC", "impact_point": -0.8, "weight": 0.06, "pct_change": -1.1}
  ],
  "items": [
    {
      "symbol": "FPT",
      "price": 123400,
      "pct_change": 1.5,
      "freefloat": 0.85,
      "capping_factor": 1.0,
      "adjusted_cap": 1000.0,
      "weight": 0.08,
      "impact_point": 1.2,
      "last_updated": "2026-08-08T09:30:00+07:00"
    }
  ]
}
~~~

index_value and total_impact_point may be null. Any absent collection in the cache is returned as an empty array or object so consumers can safely iterate missing_symbols, missing_reasons, top_positive, top_negative, and items.

### GET /index-impact/{indexCode}/history

Returns the index-impact timeline from the Redis Stream index:impact:{indexCode}:history:{date}. The endpoint reads up to 1,000 stream records in their natural stream order and parses string-valued numeric fields and the nested top_positive_json and top_negative_json fields.

| Query parameter | Required | Description |
| --- | --- | --- |
| date | No | Redis date suffix, normally YYYYMMDD; defaults to the current Asia/Ho_Chi_Minh date |

The endpoint returns 200 with an array, including an empty array when the stream has no records. Invalid indexCode values return 400.

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/index-impact/VN30/history?date=20260808"
~~~

~~~json
[
  {
    "timestamp": "2026-08-08T09:30:00+07:00",
    "index_code": "VN30",
    "index_value": 1450.5,
    "valid_count": 28,
    "missing_count": 2,
    "total_adjusted_cap": 123456.78,
    "total_impact_point": 4.2,
    "top_positive": [
      {"symbol": "FPT", "impact_point": 1.2, "weight": 0.08, "pct_change": 1.5}
    ],
    "top_negative": [
      {"symbol": "VIC", "impact_point": -0.8, "weight": 0.06, "pct_change": -1.1}
    ]
  }
]
~~~

### GET /index-valuation/{symbol}/historical

Returns index close, P/E, and P/B rows ordered by date ascending. The symbol is normalized to uppercase.

| Query parameter | Required | Description |
| --- | --- | --- |
| start_date | No | Inclusive ISO date in YYYY-MM-DD |
| end_date | No | Inclusive ISO date in YYYY-MM-DD |

start_date must not be after end_date. The endpoint returns 200 or 400.

~~~bash
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  "http://localhost:8080/stellar-api/v1/index-valuation/VNINDEX/historical?start_date=2026-01-01&end_date=2026-07-04"
~~~

### GET /index-valuation/{symbol}/latest

Returns the most recent valuation row, or 204 when no row exists for the symbol. It returns 200, 204, or 400.

~~~json
{
  "symbol": "VNINDEX",
  "date": "2026-07-04",
  "indexClose": 1280.5,
  "pe": 13.2,
  "pb": 1.7
}
~~~

### GET /index-ohlcv/{symbol}

Returns index OHLCV history for one symbol. The symbol is normalized to uppercase.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | No | Stored timeframe; default 1d |
| fromDateSk | No | Inclusive start date in YYYYMMDD |
| toDateSk | No | Inclusive end date in YYYYMMDD |
| limit | No | Default 500, maximum 5000 |
| order | No | asc or desc; default asc |

### GET /index-ohlcv

Returns index OHLCV rows for one date.

| Query parameter | Required | Description |
| --- | --- | --- |
| dateSk | Yes | Snapshot date in YYYYMMDD |
| timeframe | No | Stored timeframe; default 1d |
| limit | No | Default 100, maximum 1000 |

### GET /index-ohlcv/latest

Uses the latest available date for the selected timeframe.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | No | Stored timeframe; default 1d |
| limit | No | Default 100, maximum 1000 |

Index OHLCV objects use the same core candle fields as stock OHLCV but do not include marketCap or marketWeight:

~~~json
{
  "symbol": "VNINDEX",
  "symbolSk": 1,
  "dateSk": 20260704,
  "fullDate": "2026-07-04",
  "timeframe": "1d",
  "timeSk": 0,
  "open": 1274.2,
  "high": 1284.1,
  "low": 1270.5,
  "close": 1280.5,
  "volume": 812345678,
  "value": 21400000000000
}
~~~

## Symbols

### GET /symbols

Returns symbol metadata ordered by symbol.

| Query parameter | Required | Description |
| --- | --- | --- |
| activeOnly | No | true by default; false includes inactive or non-current records |
| limit | No | Default 500, maximum 2000 |

~~~json
[
  {
    "symbolSk": 76,
    "symbol": "FPT",
    "isActive": true,
    "sharesOutstanding": 1470000000,
    "freefloat": 0.85
  }
]
~~~

### GET /symbols/available

Returns an alphabetically ordered array of symbols that have stock OHLCV rows:

~~~json
["FPT", "HPG", "VIX"]
~~~

## Sector and market analytics

### GET /market-structure

Returns the latest market-structure cache row for a valid timeframe and benchmark.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | Yes | One of 1M, 3M, 6M, or 1Y; case-insensitive |
| benchmark | No | Benchmark name; defaults to VNINDEX |

Returns 204 when no matching cache row exists.

~~~json
{
  "dateSk": 20260704,
  "timeframe": "1M",
  "benchmark": "VNINDEX",
  "marketStructureCode": "RISK_ON",
  "marketStructureLabel": "Risk-on",
  "coreSectors": ["BANKS", "TECHNOLOGY"],
  "coreBlocks": ["FINANCIALS"],
  "topEcosystemCode": "BANKING",
  "topEcosystemName": "Banking",
  "sectorRankings": [{"sectorCode": "BANKS", "rank": 1}],
  "ecosystemRankings": [{"ecosystemCode": "BANKING", "rank": 1}],
  "ingestionTime": "2026-07-04T17:30:00"
}
~~~

The coreSectors, coreBlocks, sectorRankings, and ecosystemRankings values are stored as JSON strings and emitted as raw JSON. Their nested schema is data-mart defined.

### GET /rrg

Returns relative-rotation graph data grouped in an items wrapper.

| Query parameter | Required | Description |
| --- | --- | --- |
| regime | Yes | One of VENTURE, FLEXIBLE, or ENDURING; case-insensitive |
| benchmark | No | Defaults to VNINDEX |
| dateSk | No | Specific date in YYYYMMDD; latest is used when omitted |

Returns 204 when no items exist.

~~~json
{
  "items": [
    {
      "sectorCode": "BANKS",
      "dateSk": 20260704,
      "rs": 104.2,
      "rm": 102.8,
      "phase": "LEADING",
      "stockCount": 16,
      "totalStocks": 16,
      "sectorName": "Banks",
      "sectorNameEn": "Banks",
      "blockType": "FINANCIALS",
      "topStocksByCap": {
        "large": [{"symbol": "VCB", "rs": 110.3, "rm": 105.1}]
      },
      "benchmark": "VNINDEX",
      "ingestionTime": "2026-07-04T17:30:00",
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
~~~

topStocksByCap is emitted as raw JSON; consumers should not assume a fixed set of capitalization bucket keys.

### GET /sector-performance

Returns the latest sector-performance chart cache rows.

| Query parameter | Required | Description |
| --- | --- | --- |
| timeframe | Yes | One of 1M, 3M, 6M, or 1Y; case-insensitive |
| sectorCode | No | Optional sector filter |

Returns 204 when no matching rows exist.

~~~json
[
  {
    "sectorCode": "BANKS",
    "blockType": "FINANCIALS",
    "timeframe": "1M",
    "chartData": [
      {"date": 20260605, "value": 0.0},
      {"date": 20260606, "value": 1.2}
    ],
    "ingestionTime": "2026-07-04T17:30:00"
  }
]
~~~

chartData is emitted as raw JSON from the data mart. The sample shows the expected chart-oriented array, but its nested schema is data-mart defined.

## Status code summary

| Status | Meaning |
| --- | --- |
| 200 | Successful result, including valid empty arrays |
| 204 | No current object or no matching analytics cache row for the endpoints that support it |
| 400 | Invalid, missing, or unparsable request data |
| 401 | Missing or invalid JWT for a protected Stellar endpoint |

For endpoint-specific 400 conditions, use the parameter tables above. Database, Redis, and unexpected server failures follow Spring Boot's normal error handling and are not converted into a custom API error schema by this project.
