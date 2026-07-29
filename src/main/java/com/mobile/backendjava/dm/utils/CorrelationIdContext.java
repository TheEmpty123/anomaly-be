package com.mobile.backendjava.dm.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Holds the correlation identifier for the current request thread.
 */
public final class CorrelationIdContext {

    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String REQUEST_ATTRIBUTE = CorrelationIdContext.class.getName() + ".correlationId";
    public static final String MDC_KEY = "correlationId";

    private CorrelationIdContext() {
    }

    public static void set(String correlationId) {
        MDC.put(MDC_KEY, correlationId);
    }

    public static String get() {
        return MDC.get(MDC_KEY);
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }

    /**
     * Runs work with the supplied correlation ID and restores the worker thread's MDC afterwards.
     * This is required for message listeners and long-lived SSE connections, which do not share
     * the original HTTP request thread.
     */
    public static void runWith(String correlationId, Runnable work) {
        with(correlationId, () -> {
            work.run();
            return null;
        });
    }

    public static <T> T with(String correlationId, Supplier<T> work) {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        try {
            if (correlationId == null || correlationId.isBlank()) {
                MDC.remove(MDC_KEY);
            } else {
                MDC.put(MDC_KEY, correlationId);
            }
            return work.get();
        } finally {
            if (previousContext == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(previousContext);
            }
        }
    }
}
