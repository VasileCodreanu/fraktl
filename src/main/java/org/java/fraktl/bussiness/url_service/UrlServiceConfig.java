package org.java.fraktl.bussiness.url_service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlServiceConfig {

    private static final AtomicLong COUNTER = new AtomicLong(1_000_000_000_000L);

    @Bean
    public ConcurrentMap<String, Long> longToShortMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentMap<Long, String> shortToLongMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public UrlShortenerService urlShortenerService(
        ConcurrentMap<String, Long> longToShortMap,
        ConcurrentMap<Long, String> shortToLongMap) {
        return new UrlShortenerService(longToShortMap, shortToLongMap, COUNTER);
    }

    @Bean
    public UrlExpanderService urlExpanderService(ConcurrentMap<Long, String> shortToLongMap) {
        return new UrlExpanderService(shortToLongMap);
    }
}