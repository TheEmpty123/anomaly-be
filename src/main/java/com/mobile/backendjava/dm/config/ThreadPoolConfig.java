package com.mobile.backendjava.dm.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int QUEUE_CAPACITY = 10;

    @Bean(name = "applicationTaskExecutor")
    public ThreadPoolTaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("dm-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(mdcTaskDecorator());
        executor.initialize();
        log.info("event=configuration.task-executor.initialized executor=applicationTaskExecutor corePoolSize={} maxPoolSize={} queueCapacity={} threadNamePrefix={}",
                CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY, "dm-task-");
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(CORE_POOL_SIZE);
        scheduler.setThreadNamePrefix("dm-scheduler-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.initialize();
        log.info("event=configuration.task-scheduler.initialized poolSize={} threadNamePrefix={}",
                CORE_POOL_SIZE, "dm-scheduler-");
        return scheduler;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(
            ThreadPoolTaskExecutor applicationTaskExecutor,
            RequestTraceInterceptor requestTraceInterceptor
    ) {
        log.info("event=configuration.web-mvc-configured asyncExecutor=applicationTaskExecutor interceptor={}",
                RequestTraceInterceptor.class.getSimpleName());
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(applicationTaskExecutor);
            }

            @Override
            public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
                registry.addInterceptor(requestTraceInterceptor);
            }
        };
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatThreadPoolCustomizer() {
        return factory -> factory.addProtocolHandlerCustomizers(new TomcatProtocolHandlerCustomizer<ProtocolHandler>() {
            @Override
            public void customize(ProtocolHandler protocolHandler) {
                if (protocolHandler instanceof AbstractHttp11Protocol<?> protocol) {
                    protocol.setMinSpareThreads(CORE_POOL_SIZE);
                    protocol.setMaxThreads(MAX_POOL_SIZE);
                    log.info("event=configuration.tomcat-thread-pool-configured minSpareThreads={} maxThreads={}",
                            CORE_POOL_SIZE, MAX_POOL_SIZE);
                }
            }
        });
    }

    private TaskDecorator mdcTaskDecorator() {
        return task -> {
            Map<String, String> submittingThreadContext = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> workerThreadContext = MDC.getCopyOfContextMap();
                try {
                    if (submittingThreadContext == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(submittingThreadContext);
                    }
                    task.run();
                } finally {
                    if (workerThreadContext == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(workerThreadContext);
                    }
                }
            };
        };
    }
}
