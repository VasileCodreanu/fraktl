package org.java.fraktl.analytics.service;

import org.java.fraktl.common.event.UrlEvent;

public interface AnalyticsService {

  void record(UrlEvent event);

}
