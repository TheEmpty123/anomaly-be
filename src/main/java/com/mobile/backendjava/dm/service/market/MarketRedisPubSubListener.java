package com.mobile.backendjava.dm.service.market;

import com.mobile.backendjava.dm.service.impl.AService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MarketRedisPubSubListener extends AService implements MessageListener {

    private final HeatmapSseService heatmapSseService;

    public MarketRedisPubSubListener(HeatmapSseService heatmapSseService) {
        this.heatmapSseService = heatmapSseService;
        initLogger();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        runTask("onMarketRedisMessage",
                details(
                        detail("channel", new String(message.getChannel(), StandardCharsets.UTF_8)),
                        detail("payloadBytes", message.getBody().length)),
                () -> heatmapSseService.broadcastQuote(new String(message.getBody(), StandardCharsets.UTF_8)));
    }
}
