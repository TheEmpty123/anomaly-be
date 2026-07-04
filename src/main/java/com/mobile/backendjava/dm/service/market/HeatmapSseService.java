package com.mobile.backendjava.dm.service.market;

import com.mobile.backendjava.dm.service.impl.AService;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class HeatmapSseService extends AService {

    private static final long SSE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

    public HeatmapSseService() {
        initLogger();
    }

    public SseEmitter connect() {
        return runTask("connectHeatmapSse", detail("activeEmittersBefore", emitters.size()), () -> {
            SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
            emitters.add(emitter);
            emitter.onCompletion(() -> emitters.remove(emitter));
            emitter.onTimeout(() -> emitters.remove(emitter));
            emitter.onError(error -> emitters.remove(emitter));
            send(emitter, "ping", Map.of("ts", Instant.now().toString()));
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
        runSilentTask(() -> broadcast("ping", Map.of("ts", Instant.now().toString())));
    }

    private void broadcast(String eventName, Object data) {
        for (SseEmitter emitter : emitters) {
            send(emitter, eventName, data);
        }
    }

    private void send(SseEmitter emitter, String eventName, Object data) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event().name(eventName);
            if (data instanceof String) {
                event.data(data);
            } else {
                event.data(data, MediaType.APPLICATION_JSON);
            }
            emitter.send(event);
        } catch (IOException | IllegalStateException ex) {
            emitters.remove(emitter);
        }
    }
}
