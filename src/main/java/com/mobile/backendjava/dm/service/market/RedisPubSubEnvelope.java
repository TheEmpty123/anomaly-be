package com.mobile.backendjava.dm.service.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Correlation-aware Redis Pub/Sub payload. Redis Pub/Sub has no message headers, so causal
 * context is carried in the JSON envelope and restored by the subscriber.
 */
public record RedisPubSubEnvelope(String correlationId, String eventType, String dataJson, boolean wrapped) {

    private static final String CORRELATION_ID_FIELD = "correlation_id";
    private static final String EVENT_TYPE_FIELD = "event_type";
    private static final String DATA_FIELD = "data";

    public static RedisPubSubEnvelope parse(String payload, ObjectMapper objectMapper) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        if (root != null && root.isObject() && root.has(CORRELATION_ID_FIELD) && root.has(DATA_FIELD)) {
            JsonNode data = root.get(DATA_FIELD);
            String dataJson = data.isTextual() ? data.asText() : objectMapper.writeValueAsString(data);
            String eventType = root.path(EVENT_TYPE_FIELD).asText("unknown");
            return new RedisPubSubEnvelope(root.path(CORRELATION_ID_FIELD).asText(null), eventType, dataJson, true);
        }
        return new RedisPubSubEnvelope(null, "legacy", payload, false);
    }

    public static String wrap(String correlationId, String eventType, Object data, ObjectMapper objectMapper) throws Exception {
        ObjectNode envelope = objectMapper.createObjectNode();
        envelope.put(CORRELATION_ID_FIELD, correlationId);
        envelope.put(EVENT_TYPE_FIELD, eventType);
        envelope.set(DATA_FIELD, objectMapper.valueToTree(data));
        return objectMapper.writeValueAsString(envelope);
    }
}
