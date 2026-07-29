package com.mobile.backendjava.dm.service.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.service.impl.AService;
import com.mobile.backendjava.dm.utils.CorrelationIdContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class MarketRedisPubSubListener extends AService implements MessageListener {

    private final HeatmapSseService heatmapSseService;
    private final ObjectMapper objectMapper;

    public MarketRedisPubSubListener(HeatmapSseService heatmapSseService, ObjectMapper objectMapper) {
        this.heatmapSseService = heatmapSseService;
        this.objectMapper = objectMapper;
        initLogger();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            RedisPubSubEnvelope envelope = RedisPubSubEnvelope.parse(payload, objectMapper);
            CorrelationIdContext.runWith(envelope.correlationId(), () -> {
                log.info("event=redis.message.received channel={} payloadBytes={} wrapped={} eventType={}",
                        channel, message.getBody().length, envelope.wrapped(), envelope.eventType());
                runTask("onMarketRedisMessage",
                        details(
                                detail("channel", channel),
                                detail("payloadBytes", message.getBody().length),
                                detail("wrapped", envelope.wrapped()),
                                detail("eventType", envelope.eventType())),
                        () -> heatmapSseService.broadcastQuote(envelope.dataJson()));
            });
        } catch (Exception ex) {
            log.error("event=redis.message.rejected channel={} payloadBytes={} errorType={} errorMessage={}",
                    channel, message.getBody().length, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }
}
