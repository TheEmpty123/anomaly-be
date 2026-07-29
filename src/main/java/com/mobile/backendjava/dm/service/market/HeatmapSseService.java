package com.mobile.backendjava.dm.service.market;

import com.mobile.backendjava.dm.service.impl.AService;
import com.mobile.backendjava.dm.utils.CorrelationIdContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
@Slf4j
public class HeatmapSseService extends AService {

    private static final long SSE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);
    private final Map<SseEmitter, SseConnection> emitters = new ConcurrentHashMap<>();

    public HeatmapSseService() {
        initLogger();
    }

    public SseEmitter connect() {
        return runTask("connectHeatmapSse", detail("activeEmittersBefore", emitters.size()), () -> {
            SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
            SseConnection connection = new SseConnection(UUID.randomUUID().toString(), CorrelationIdContext.get());
            emitters.put(emitter, connection);
            log.info("event=sse.connection.opened connectionId={} activeConnections={} timeoutMs={}",
                    connection.connectionId(), emitters.size(), SSE_TIMEOUT_MS);
            emitter.onCompletion(() -> closeConnection(emitter, "completed", null));
            emitter.onTimeout(() -> closeConnection(emitter, "timeout", null));
            emitter.onError(error -> closeConnection(emitter, "error", error));
            send(emitter, connection, "ping", Map.of("ts", Instant.now().toString()), connection.correlationId(), true);
            return emitter;
        });
    }

    public void broadcastQuote(String quoteJson) {
        runTask("broadcastHeatmapQuote",
                details(detail("activeEmitters", emitters.size()), detail("payloadChars", quoteJson == null ? 0 : quoteJson.length())),
                () -> broadcast("quote", quoteJson));
    }

    @Scheduled(fixedRate = 20000)
    public void heartbeat() {
        // Keep the SSE connection alive, but temporarily suppress heartbeat lifecycle logs.
        broadcast("ping", Map.of("ts", Instant.now().toString()), false);
    }

    private void broadcast(String eventName, Object data) {
        broadcast(eventName, data, true);
    }

    private void broadcast(String eventName, Object data, boolean logLifecycle) {
        String sourceCorrelationId = CorrelationIdContext.get();
        if (logLifecycle) {
            log.info("event=sse.broadcast.start eventName={} connectionCount={} sourceCorrelationId={}",
                    eventName, emitters.size(), sourceCorrelationId);
        }
        for (Map.Entry<SseEmitter, SseConnection> entry : emitters.entrySet()) {
            send(entry.getKey(), entry.getValue(), eventName, data, sourceCorrelationId, logLifecycle);
        }
        if (logLifecycle) {
            log.info("event=sse.broadcast.finish eventName={} connectionCount={} sourceCorrelationId={}",
                    eventName, emitters.size(), sourceCorrelationId);
        }
    }

    private void send(SseEmitter emitter, SseConnection connection, String eventName, Object data,
                      String sourceCorrelationId, boolean logLifecycle) {
        CorrelationIdContext.runWith(connection.correlationId(), () -> {
            try {
                if (logLifecycle) {
                    log.info("event=sse.event.send connectionId={} eventName={} payloadType={} sourceCorrelationId={}",
                            connection.connectionId(), eventName, data == null ? "null" : data.getClass().getSimpleName(), sourceCorrelationId);
                }
                SseEmitter.SseEventBuilder event = SseEmitter.event().name(eventName);
                if (data instanceof String) {
                    event.data(data);
                } else {
                    event.data(data, MediaType.APPLICATION_JSON);
                }
                emitter.send(event);
                if (logLifecycle) {
                    log.info("event=sse.event.sent connectionId={} eventName={} sourceCorrelationId={}",
                            connection.connectionId(), eventName, sourceCorrelationId);
                }
            } catch (IOException | IllegalStateException ex) {
                log.warn("event=sse.event.failed connectionId={} eventName={} sourceCorrelationId={} errorType={} errorMessage={}",
                        connection.connectionId(), eventName, sourceCorrelationId,
                        ex.getClass().getSimpleName(), ex.getMessage());
                closeConnection(emitter, "send-failed", ex);
            }
        });
    }

    private void closeConnection(SseEmitter emitter, String reason, Throwable error) {
        SseConnection connection = emitters.remove(emitter);
        if (connection == null) {
            return;
        }
        CorrelationIdContext.runWith(connection.correlationId(), () -> {
            if (error == null) {
                log.info("event=sse.connection.closed connectionId={} reason={} activeConnections={}",
                        connection.connectionId(), reason, emitters.size());
            } else {
                log.warn("event=sse.connection.closed connectionId={} reason={} activeConnections={} errorType={} errorMessage={}",
                        connection.connectionId(), reason, emitters.size(), error.getClass().getSimpleName(), error.getMessage());
            }
        });
    }

    private record SseConnection(String connectionId, String correlationId) {
    }
}
