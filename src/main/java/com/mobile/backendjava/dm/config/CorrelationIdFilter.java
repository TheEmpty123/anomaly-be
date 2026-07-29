package com.mobile.backendjava.dm.config;

import com.mobile.backendjava.dm.utils.CorrelationIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * Creates a request-scoped identifier before authentication or controller code runs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = existingOrNewCorrelationId(request);
        long startedAt = System.currentTimeMillis();
        request.setAttribute(CorrelationIdContext.REQUEST_ATTRIBUTE, correlationId);
        response.setHeader(CorrelationIdContext.HEADER_NAME, correlationId);
        CorrelationIdContext.set(correlationId);

        try {
            log.info("event=request.start dispatchType={} method={} path={} query={} remoteAddress={}",
                    request.getDispatcherType(), request.getMethod(), request.getRequestURI(), safeQuery(request), request.getRemoteAddr());
            filterChain.doFilter(request, response);
        } finally {
            log.info("event=request.finish dispatchType={} method={} path={} status={} durationMs={}",
                    request.getDispatcherType(), request.getMethod(), request.getRequestURI(), response.getStatus(), System.currentTimeMillis() - startedAt);
            CorrelationIdContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    private String existingOrNewCorrelationId(HttpServletRequest request) {
        Object existingId = request.getAttribute(CorrelationIdContext.REQUEST_ATTRIBUTE);
        return existingId instanceof String correlationId && !correlationId.isBlank()
                ? correlationId
                : UUID.randomUUID().toString();
    }

    private String safeQuery(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return "none";
        }
        return query.replaceAll("(?i)(token|access_token|authorization|password)=([^&]*)", "$1=REDACTED");
    }
}
