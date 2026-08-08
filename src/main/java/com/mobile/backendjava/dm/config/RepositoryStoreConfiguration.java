package com.mobile.backendjava.dm.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Keeps Spring Data modules from attempting to assign repositories owned by a
 * different store.
 */
@Configuration
@EntityScan(basePackages = RepositoryStoreConfiguration.JPA_ENTITIES_PACKAGE)
@EnableJpaRepositories(basePackages = RepositoryStoreConfiguration.JPA_REPOSITORIES_PACKAGE)
@EnableRedisRepositories(basePackages = RepositoryStoreConfiguration.REDIS_REPOSITORIES_PACKAGE)
@Slf4j
public class RepositoryStoreConfiguration {

    static final String JPA_ENTITIES_PACKAGE = "com.mobile.backendjava.dm.entities";
    static final String JPA_REPOSITORIES_PACKAGE = "com.mobile.backendjava.dm.repository.jpa";
    static final String REDIS_REPOSITORIES_PACKAGE = "com.mobile.backendjava.dm.repository.redis";

    @PostConstruct
    void logRepositoryScanConfiguration() {
        log.info("event=configuration.repository-scan-configured jpaRepositoriesPackage={} redisRepositoriesPackage={}",
                JPA_REPOSITORIES_PACKAGE, REDIS_REPOSITORIES_PACKAGE);
    }
}
