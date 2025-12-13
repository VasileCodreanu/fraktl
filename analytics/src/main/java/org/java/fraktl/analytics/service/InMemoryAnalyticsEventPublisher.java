package org.java.fraktl.analytics.service;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.common.event.UrlEvent;
import org.java.fraktl.common.ports.out.AnalyticsEventPublisherPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryAnalyticsEventPublisher implements AnalyticsEventPublisherPort {

  @Override
  public void publishUrlEvent(UrlEvent event) {
    ArrayList<UrlEvent> urlEvents = new ArrayList<>();
    urlEvents.add(event);
    System.out.println(urlEvents);

  }
}