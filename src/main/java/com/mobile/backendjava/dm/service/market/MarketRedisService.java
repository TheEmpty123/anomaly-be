package com.mobile.backendjava.dm.service.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.dto.market.HeatmapQuoteDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactHistoryPointDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactRankedItemDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactSnapshotDTO;
import com.mobile.backendjava.dm.service.impl.AService;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Locale;

@Service
public class MarketRedisService extends AService {

    private static final ZoneId MARKET_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_KEY_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final int MAX_BREADTH_HISTORY_POINTS = 1000;
    private static final int MAX_INDEX_IMPACT_HISTORY_POINTS = 1000;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public MarketRedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        initLogger();
    }

    public List<HeatmapQuoteDTO> getHeatmapSnapshot() {
        return runTask("getHeatmapSnapshot", detail("redisPattern", "stock:quote:*"), () -> {
            List<HeatmapQuoteDTO> quotes = new ArrayList<>();
            try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("stock:quote:*").count(500).build())) {
                while (cursor.hasNext()) {
                    String quoteKey = cursor.next();
                    Map<String, Object> quote = readObject(quoteKey);
                    if (quote == null || quote.isEmpty()) {
                        continue;
                    }
                    String symbol = stringValue(firstNonNull(quote, "symbol", "ticker"));
                    if (symbol == null || symbol.isBlank()) {
                        symbol = quoteKey.substring("stock:quote:".length());
                    }
                    Map<String, Object> meta = readObject("stock:meta:" + symbol);
                    Map<String, Object> merged = new LinkedHashMap<>();
                    if (meta != null) {
                        merged.putAll(meta);
                    }
                    merged.putAll(quote);
                    quotes.add(toHeatmapQuote(symbol, merged));
                }
            }
            return quotes;
        });
    }

    public Object getCurrentBreadth() {
        return runTask("getCurrentBreadth", detail("date", normalizeDate(null)), () -> getBreadth(null, false));
    }

    public Object getBreadthHistory(String date) {
        return runTask("getBreadthHistory", detail("date", normalizeDate(date)), () -> getBreadth(date, true));
    }

    public Optional<IndexImpactSnapshotDTO> getLatestIndexImpact(String indexCode) {
        String normalizedIndexCode = normalizeIndexCode(indexCode);
        return runTask("getLatestIndexImpact",
                details(detail("indexCode", normalizedIndexCode)),
                () -> {
                    String key = "index:impact:" + normalizedIndexCode + ":latest";
                    String payload = redisTemplate.opsForValue().get(key);
                    if (isBlankOrNull(payload)) {
                        return Optional.empty();
                    }

                    try {
                        IndexImpactSnapshotDTO snapshot = objectMapper.readValue(payload, IndexImpactSnapshotDTO.class);
                        normalizeSnapshot(snapshot, normalizedIndexCode);
                        return Optional.of(snapshot);
                    } catch (JsonProcessingException ex) {
                        throw new IllegalStateException("Invalid index-impact snapshot payload for " + normalizedIndexCode, ex);
                    }
                });
    }

    public List<IndexImpactHistoryPointDTO> getIndexImpactHistory(String indexCode, String date) {
        String normalizedIndexCode = normalizeIndexCode(indexCode);
        String dateKey = normalizeDate(date);
        return runTask("getIndexImpactHistory",
                details(detail("indexCode", normalizedIndexCode), detail("date", dateKey)),
                () -> {
                    String key = "index:impact:" + normalizedIndexCode + ":history:" + dateKey;
                    List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                            .range(key, Range.unbounded(), Limit.limit().count(MAX_INDEX_IMPACT_HISTORY_POINTS));
                    if (records == null || records.isEmpty()) {
                        return List.of();
                    }
                    return records.stream()
                            .map(record -> toIndexImpactHistoryPoint(record.getValue(), normalizedIndexCode))
                            .toList();
                });
    }

    private Object getBreadth(String date, boolean history) {
        String dateKey = normalizeDate(date);
        String key = history ? "market:breadth:history:" + dateKey : "market:breadth:" + dateKey;
        Object value = readAny(key);
        if (value instanceof Map<?, ?> map) {
            return enrichBreadthMap(map);
        }
        if (history && value instanceof List<?> list) {
            return list.stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .map(this::enrichBreadthMap)
                    .toList();
        }
        return value;
    }

    private String normalizeDate(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now(MARKET_ZONE).format(DATE_KEY_FORMAT);
        }
        return date.trim();
    }

    private String normalizeIndexCode(String indexCode) {
        if (indexCode == null || indexCode.isBlank()) {
            throw new IllegalArgumentException("indexCode must not be blank");
        }
        String normalized = indexCode.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z0-9._-]+")) {
            throw new IllegalArgumentException("indexCode contains unsupported characters");
        }
        return normalized;
    }

    private void normalizeSnapshot(IndexImpactSnapshotDTO snapshot, String requestedIndexCode) {
        if (snapshot == null) {
            throw new IllegalStateException("Index-impact snapshot payload must be a JSON object");
        }
        if (isBlankOrNull(snapshot.getIndexCode())) {
            snapshot.setIndexCode(requestedIndexCode);
        }
        if (snapshot.getMissingSymbols() == null) {
            snapshot.setMissingSymbols(List.of());
        }
        if (snapshot.getMissingReasons() == null) {
            snapshot.setMissingReasons(Map.of());
        }
        if (snapshot.getTopPositive() == null) {
            snapshot.setTopPositive(List.of());
        }
        if (snapshot.getTopNegative() == null) {
            snapshot.setTopNegative(List.of());
        }
        if (snapshot.getItems() == null) {
            snapshot.setItems(List.of());
        }
    }

    private IndexImpactHistoryPointDTO toIndexImpactHistoryPoint(Map<Object, Object> fields, String requestedIndexCode) {
        String indexCode = textValue(fields, "index_code");
        return IndexImpactHistoryPointDTO.builder()
                .timestamp(textValue(fields, "timestamp"))
                .indexCode(isBlankOrNull(indexCode) ? requestedIndexCode : indexCode)
                .indexValue(decimalValue(fields, "index_value"))
                .validCount(integerValue(fields, "valid_count"))
                .missingCount(integerValue(fields, "missing_count"))
                .totalAdjustedCap(decimalValue(fields, "total_adjusted_cap"))
                .totalImpactPoint(decimalValue(fields, "total_impact_point"))
                .topPositive(rankedItems(fields, "top_positive_json"))
                .topNegative(rankedItems(fields, "top_negative_json"))
                .build();
    }

    private List<IndexImpactRankedItemDTO> rankedItems(Map<Object, Object> fields, String fieldName) {
        String json = textValue(fields, fieldName);
        if (isBlankOrNull(json)) {
            return List.of();
        }
        try {
            List<IndexImpactRankedItemDTO> items = objectMapper.readValue(
                    json,
                    new TypeReference<List<IndexImpactRankedItemDTO>>() {
                    });
            return items == null ? List.of() : items;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Invalid " + fieldName + " in index-impact history record", ex);
        }
    }

    private Double decimalValue(Map<Object, Object> fields, String fieldName) {
        String value = textValue(fields, fieldName);
        if (isBlankOrNull(value)) {
            return null;
        }
        try {
            Double parsed = Double.valueOf(value.trim());
            if (!Double.isFinite(parsed)) {
                throw new NumberFormatException("non-finite number");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid decimal " + fieldName + " in index-impact history record", ex);
        }
    }

    private Integer integerValue(Map<Object, Object> fields, String fieldName) {
        String value = textValue(fields, fieldName);
        if (isBlankOrNull(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid integer " + fieldName + " in index-impact history record", ex);
        }
    }

    private String textValue(Map<Object, Object> fields, String fieldName) {
        Object value = fields.get(fieldName);
        return value == null ? null : String.valueOf(value);
    }

    private boolean isBlankOrNull(String value) {
        return value == null || value.isBlank() || "null".equalsIgnoreCase(value.trim());
    }

    private Map<String, Object> readObject(String key) {
        Object value = readAny(key);
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        return null;
    }

    private Object readAny(String key) {
        DataType type = redisTemplate.type(key);
        if (DataType.STRING.equals(type)) {
            String json = redisTemplate.opsForValue().get(key);
            return parseJson(json);
        }
        if (DataType.HASH.equals(type)) {
            return new LinkedHashMap<>(redisTemplate.opsForHash().entries(key));
        }
        if (DataType.STREAM.equals(type)) {
            return readStream(key);
        }
        return null;
    }

    private List<Map<String, Object>> readStream(String key) {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .range(key, Range.unbounded(), Limit.limit().count(MAX_BREADTH_HISTORY_POINTS));
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        return records.stream()
                .map(record -> {
                    Map<String, Object> values = new LinkedHashMap<>();
                    values.put("streamId", record.getId().getValue());
                    record.getValue().forEach((k, v) -> values.put(String.valueOf(k), v));
                    return values;
                })
                .toList();
    }

    private Object parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception ex) {
            log.warn("event=service.decision service=MarketRedisService action=parseJson outcome=ignored reason=invalid-json errorType={} errorMessage={}",
                    ex.getClass().getSimpleName(), ex.getMessage());
            return null;
        }
    }

    private HeatmapQuoteDTO toHeatmapQuote(String symbol, Map<String, Object> values) {
        return HeatmapQuoteDTO.builder()
                .symbol(symbol)
                .price(decimal(firstNonNull(values, "price", "lastPrice", "last_price")))
                .refPrice(decimal(firstNonNull(values, "refPrice", "ref_price", "referencePrice", "reference_price")))
                .pctChange(decimal(firstNonNull(values, "pctChange", "pct_change", "percentChange", "percent_change")))
                .volume(longValue(firstNonNull(values, "volume", "totalVolume", "total_volume")))
                .txnValue(decimal(firstNonNull(values, "txnValue", "txn_value", "tradingValue", "trading_value")))
                .marketCap(decimal(firstNonNull(values, "marketCap", "market_cap")))
                .status(stringValue(firstNonNull(values, "status")))
                .sector(stringValue(firstNonNull(values, "sector")))
                .industry(stringValue(firstNonNull(values, "industry")))
                .exchange(stringValue(firstNonNull(values, "exchange")))
                .lastUpdated(stringValue(firstNonNull(values, "lastUpdated", "last_updated", "timestamp", "updatedAt", "updated_at")))
                .build();
    }

    private Map<String, Object> enrichBreadthMap(Map<?, ?> map) {
        Map<String, Object> enriched = new LinkedHashMap<>();
        map.forEach((k, v) -> enriched.put(String.valueOf(k), v));
        addBreadthPercentages(enriched);
        return enriched;
    }

    private void addBreadthPercentages(Map<String, Object> values) {
        BigDecimal advance = decimal(firstNonNull(values, "advance", "advances"));
        BigDecimal decline = decimal(firstNonNull(values, "decline", "declines"));
        BigDecimal unchanged = decimal(firstNonNull(values, "unchanged", "noChange", "no_change"));
        BigDecimal total = decimal(firstNonNull(values, "total", "totalStocks", "total_stocks"));
        if (total == null) {
            total = BigDecimal.ZERO;
            if (advance != null) total = total.add(advance);
            if (decline != null) total = total.add(decline);
            if (unchanged != null) total = total.add(unchanged);
        }
        values.put("advancePct", percentage(advance, total));
        values.put("declinePct", percentage(decline, total));
        values.put("unchangedPct", percentage(unchanged, total));
    }

    private BigDecimal percentage(BigDecimal value, BigDecimal total) {
        if (value == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.multiply(BigDecimal.valueOf(100)).divide(total, 4, java.math.RoundingMode.HALF_UP);
    }

    private Object firstNonNull(Map<String, Object> values, String... keys) {
        for (String key : keys) {
            Object value = values.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private BigDecimal decimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(text.replace(",", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(text.replace(",", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
