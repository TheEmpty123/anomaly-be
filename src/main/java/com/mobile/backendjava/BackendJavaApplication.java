package com.mobile.backendjava;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class BackendJavaApplication {

    public static void main(String[] args) {
        log.info("event=application.bootstrap.start application=backend-java");
        try {
            ConfigurableApplicationContext context = SpringApplication.run(BackendJavaApplication.class, args);
            log.info("event=application.bootstrap.ready application=backend-java activeProfiles={}",
                    String.join(",", context.getEnvironment().getActiveProfiles()));
        } catch (RuntimeException ex) {
            log.error("event=application.bootstrap.failed application=backend-java errorType={} errorMessage={}",
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;
        }
    }

}
