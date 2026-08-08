package com.mobile.backendjava.dm.controllers.stellar;

import com.mobile.backendjava.dm.dto.market.IndexImpactHistoryPointDTO;
import com.mobile.backendjava.dm.dto.market.IndexImpactSnapshotDTO;
import com.mobile.backendjava.dm.service.market.MarketRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StellarIndexImpactControllerTest {

    @Test
    void latestReturnsNoContentWhenRedisSnapshotIsMissing() {
        MarketRedisService service = mock(MarketRedisService.class);
        when(service.getLatestIndexImpact("VN30")).thenReturn(Optional.empty());
        StellarIndexImpactController controller = new StellarIndexImpactController(service);

        ResponseEntity<IndexImpactSnapshotDTO> response = controller.latest("VN30");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).getLatestIndexImpact("VN30");
    }

    @Test
    void historyReturnsTimelineForRequestedDate() {
        MarketRedisService service = mock(MarketRedisService.class);
        List<IndexImpactHistoryPointDTO> expected = List.of(
                IndexImpactHistoryPointDTO.builder().indexCode("VN30").timestamp("2026-08-08T09:30:00+07:00").build());
        when(service.getIndexImpactHistory("VN30", "20260808")).thenReturn(expected);
        StellarIndexImpactController controller = new StellarIndexImpactController(service);

        ResponseEntity<List<IndexImpactHistoryPointDTO>> response = controller.history("VN30", "20260808");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(service).getIndexImpactHistory("VN30", "20260808");
    }
}
