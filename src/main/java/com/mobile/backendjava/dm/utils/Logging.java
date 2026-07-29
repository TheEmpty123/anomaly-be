package com.mobile.backendjava.dm.utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Logging {
    public static boolean shouldLog = true;

    public static void log(Object msg) {
        if (shouldLog) {
            log.info("{}", msg);
        }
    }

    public static void log(Object message, Object context) {
        if (shouldLog) {
            log.info("{} {}", message, context);
        }
    }

    public static void warn(Object msg) {
        if (shouldLog) {
            log.warn("{}", msg);
        }
    }

    public static void warn(Object message, Object context) {
        if (shouldLog) {
            log.warn("{} {}", message, context);
        }
    }

    public static void error(Object msg) {
        if (shouldLog) {
            log.error("{}", msg);
        }
    }

    public static void error(Object message, Object context) {
        if (shouldLog) {
            log.error("{} {}", message, context);
        }
    }
}
