package com.fraktl.urlmanagement.config;

import com.fraktl.urlmanagement.service.impl.helpers.UrlExpander;
import com.fraktl.urlmanagement.service.impl.helpers.UrlShortener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlManagerConfig {

  @Bean
  public UrlExpander urlExpander() {
    return new UrlExpander();
  }

  @Bean
  public UrlShortener urlShortener() {
    return new UrlShortener();
  }

}
