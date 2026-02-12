package com.fraktl.analytics.adapter;

import lombok.RequiredArgsConstructor;
import com.fraktl.analytics.service.AnalyticsService;
import com.fraktl.common.event.UrlEventMessage;
import com.fraktl.common.ports.out.AnalyticsEventPublisherPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaAnalyticsEventPublisher implements AnalyticsEventPublisherPort {

  private final AnalyticsService analyticsService;

  @Override
  public void publishUrlEvent(UrlEventMessage event) {
    analyticsService.record(event);
  }

}