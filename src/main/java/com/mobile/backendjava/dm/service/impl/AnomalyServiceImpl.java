package com.mobile.backendjava.dm.service.impl;

import com.mobile.backendjava.dm.dto.anomaly.AnomalyDTO;
import com.mobile.backendjava.dm.repository.AnomalyRepository;
import com.mobile.backendjava.dm.service.AnomalyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnomalyServiceImpl extends AService implements AnomalyService {

    private final AnomalyRepository anomalyRepository;

    public AnomalyServiceImpl(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
        initLogger();
    }

    @Override
    public void initLogger() {
        super.initLogger();
    }

    @Override
    public List<AnomalyDTO> getAnomalies() {
        return runTask("getAnomalies", anomalyRepository::findAll);
    }
}
