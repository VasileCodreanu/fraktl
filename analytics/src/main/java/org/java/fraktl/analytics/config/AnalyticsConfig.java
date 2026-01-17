package org.java.fraktl.analytics.config;

import org.java.fraktl.analytics.helper.UrlEventConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsConfig {

  @Bean
  public UrlEventConverter urlEventConverter() {
    return new UrlEventConverter();
  }

}
