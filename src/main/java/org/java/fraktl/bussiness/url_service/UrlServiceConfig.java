package org.java.fraktl.bussiness.url_service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlServiceConfig {


    private static final AtomicLong COUNTER = new AtomicLong(1_000_000_000_000L);

    @Bean
    public AtomicLong getCOUNTER() {
        return COUNTER;
    }
}