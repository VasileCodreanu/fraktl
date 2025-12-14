package org.java.fraktl.analytics.adapter;

import lombok.RequiredArgsConstructor;
import org.java.fraktl.analytics.service.AnalyticsService;
import org.java.fraktl.common.event.UrlEvent;
import org.java.fraktl.common.ports.out.AnalyticsEventPublisherPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaAnalyticsEventPublisher implements AnalyticsEventPublisherPort {

  private final AnalyticsService analyticsService;

  @Override
  public void publishUrlEvent(UrlEvent event) {
    analyticsService.record(event);
  }

}