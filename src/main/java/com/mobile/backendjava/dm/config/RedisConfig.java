package com.mobile.backendjava.dm.config;

import com.mobile.backendjava.dm.service.market.MarketRedisPubSubListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Slf4j
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        log.info("event=configuration.redis.connection-factory-configured host={} port={}", host, port);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("event=configuration.redis.template-configured template=StringRedisTemplate");
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            MarketRedisPubSubListener marketRedisPubSubListener,
            @Value("${app.market.pubsub-channel}") String channel) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(marketRedisPubSubListener, new ChannelTopic(channel));
        log.info("event=configuration.redis.pubsub-listener-configured channel={}", channel);
        return container;
    }
}
