package com.mobile.backendjava.dm.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Adds controller-level trace events without duplicating logging in every endpoint method.
 */
@Component
@Slf4j
public class RequestTraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("event=handler.start handler={} method={} path={}", handlerName(handler), request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex == null) {
            log.info("event=handler.finish handler={} status={}", handlerName(handler), response.getStatus());
        } else {
            log.error("event=handler.finish handler={} status={} outcome=failure errorType={} errorMessage={}",
                    handlerName(handler), response.getStatus(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    private String handlerName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName();
        }
        return handler.getClass().getSimpleName();
    }
}
