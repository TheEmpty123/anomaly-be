package com.mobile.backendjava.dm.repository;

import com.mobile.backendjava.dm.dto.anomaly.AIAnalysisDTO;
import com.mobile.backendjava.dm.dto.anomaly.AnomalyDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnomalyRepository {

    public List<AnomalyDTO> findAll() {
        return List.of(
                AnomalyDTO.builder()
                        .symbol("VIX")
                        .date("2026-04-22")
                        .aiAnalysis(AIAnalysisDTO.builder()
                                .anomalyScore(0.88)
                                .isAnomaly(true)
                                .statusLabel("CẢNH BÁO")
                                .build())
                        .explanation("Khối lượng giao dịch cao gấp 3.8 lần trung bình 20 phiên, nhưng giá gần như không đổi. Dấu hiệu có lệnh trao tay hoặc tổ chức gom hàng khối lượng lớn.")
                        .build(),
                AnomalyDTO.builder()
                        .symbol("HAG")
                        .date("2026-04-22")
                        .aiAnalysis(AIAnalysisDTO.builder()
                                .anomalyScore(0.92)
                                .isAnomaly(true)
                                .statusLabel("THAO TÚNG")
                                .build())
                        .explanation("Giá giảm sàn (-6.8%) với biên độ dao động trong phiên cực rộng, nhưng thanh khoản lại cạn kiệt. Khả năng cao bị đạp giá ảo do thiếu lực cầu.")
                        .build(),
                AnomalyDTO.builder()
                        .symbol("FPT")
                        .date("2026-04-22")
                        .aiAnalysis(AIAnalysisDTO.builder()
                                .anomalyScore(0.15)
                                .isAnomaly(false)
                                .statusLabel("BÌNH THƯỜNG")
                                .build())
                        .explanation("Biến động giá và khối lượng giao dịch nằm trong vùng phân phối chuẩn của 5 năm lịch sử. Dòng tiền ổn định.")
                        .build()
        );
    }
}
