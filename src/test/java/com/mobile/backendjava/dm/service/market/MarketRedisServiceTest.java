package com.mobile.backendjava.dm.service.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.dto.market.IndexImpactHistoryPointDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactSnapshotDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketRedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @Mock
    private MapRecord<String, Object, Object> streamRecord;

    private ObjectMapper objectMapper;
    private MarketRedisService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new MarketRedisService(redisTemplate, objectMapper);
    }

    @Test
    void getLatestIndexImpactDeserializesWorkerSnapshot() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("index:impact:VN30:latest")).thenReturn("""
                {
                  "index_code": "VN30",
                  "timestamp": "2026-08-08T09:30:00+07:00",
                  "index_value": 1450.5,
                  "valid_count": 28,
                  "missing_count": 2,
                  "missing_symbols": ["ABC"],
                  "missing_reasons": {"ABC": "missing quote"},
                  "total_adjusted_cap": 123456.78,
                  "total_impact_point": 4.2,
                  "top_positive": [{"symbol": "FPT", "impact_point": 1.2, "weight": 0.08, "pct_change": 1.5}],
                  "top_negative": [],
                  "items": [{"symbol": "FPT", "price": 123400, "pct_change": 1.5, "freefloat": 0.85,
                             "capping_factor": 1.0, "adjusted_cap": 1000.0, "weight": 0.08,
                             "impact_point": 1.2, "last_updated": "2026-08-08T09:30:00+07:00"}]
                }
                """);

        Optional<IndexImpactSnapshotDTO> result = service.getLatestIndexImpact("vn30");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow())
                .extracting(IndexImpactSnapshotDTO::getIndexCode,
                        IndexImpactSnapshotDTO::getIndexValue,
                        IndexImpactSnapshotDTO::getValidCount,
                        IndexImpactSnapshotDTO::getTotalImpactPoint)
                .containsExactly("VN30", 1450.5, 28, 4.2);
        assertThat(result.orElseThrow().getTopPositive()).singleElement()
                .extracting(item -> item.getSymbol(), item -> item.getImpactPoint(), item -> item.getPctChange())
                .containsExactly("FPT", 1.2, 1.5);
        assertThat(result.orElseThrow().getItems()).singleElement()
                .extracting(item -> item.getCappingFactor(), item -> item.getLastUpdated())
                .containsExactly(1.0, "2026-08-08T09:30:00+07:00");
        assertThat(objectMapper.writeValueAsString(result.orElseThrow()))
                .contains("\"index_code\":\"VN30\"", "\"total_impact_point\":4.2")
                .doesNotContain("\"indexCode\"", "\"totalImpactPoint\"");
    }

    @Test
    void getIndexImpactHistoryParsesStringStreamFields() {
        Map<Object, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", "2026-08-08T09:30:00+07:00");
        fields.put("index_code", "VN30");
        fields.put("index_value", "1450.5");
        fields.put("valid_count", "28");
        fields.put("missing_count", "2");
        fields.put("total_adjusted_cap", "123456.78");
        fields.put("total_impact_point", "4.2");
        fields.put("top_positive_json", "[{\"symbol\":\"FPT\",\"impact_point\":1.2,\"weight\":0.08,\"pct_change\":1.5}]");
        fields.put("top_negative_json", "[{\"symbol\":\"VIC\",\"impact_point\":-0.8,\"weight\":0.06,\"pct_change\":-1.1}]");

        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(streamOperations.range(eq("index:impact:VN30:history:20260808"), any(), any(Limit.class)))
                .thenReturn(List.of(streamRecord));
        when(streamRecord.getValue()).thenReturn(fields);

        List<IndexImpactHistoryPointDTO> history = service.getIndexImpactHistory("vn30", "20260808");

        assertThat(history).singleElement()
                .extracting(IndexImpactHistoryPointDTO::getIndexCode,
                        IndexImpactHistoryPointDTO::getIndexValue,
                        IndexImpactHistoryPointDTO::getValidCount,
                        IndexImpactHistoryPointDTO::getTotalAdjustedCap)
                .containsExactly("VN30", 1450.5, 28, 123456.78);
        assertThat(history.get(0).getTopPositive()).singleElement()
                .extracting(item -> item.getSymbol(), item -> item.getImpactPoint())
                .containsExactly("FPT", 1.2);
        assertThat(history.get(0).getTopNegative()).singleElement()
                .extracting(item -> item.getSymbol(), item -> item.getPctChange())
                .containsExactly("VIC", -1.1);
        verify(streamOperations).range(eq("index:impact:VN30:history:20260808"), any(), any(Limit.class));
    }

    @Test
    void getLatestIndexImpactRejectsUnsupportedIndexCode() {
        assertThatThrownBy(() -> service.getLatestIndexImpact("VN30:latest"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("indexCode contains unsupported characters");
    }
}
