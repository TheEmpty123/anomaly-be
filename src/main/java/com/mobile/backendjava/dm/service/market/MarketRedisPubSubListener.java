package com.mobile.backendjava.dm.service.market;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MarketRedisPubSubListener implements MessageListener {

    private final HeatmapSseService heatmapSseService;

    public MarketRedisPubSubListener(HeatmapSseService heatmapSseService) {
        this.heatmapSseService = heatmapSseService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        heatmapSseService.broadcastQuote(new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
