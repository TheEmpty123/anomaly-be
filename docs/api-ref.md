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
      "regime": "FLEXIBLE"
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