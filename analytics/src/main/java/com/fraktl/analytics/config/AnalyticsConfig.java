package com.fraktl.analytics.config;

import com.fraktl.analytics.helper.UrlEventConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsConfig {

  @Bean
  public UrlEventConverter urlEventConverter() {
    return new UrlEventConverter();
  }

}
