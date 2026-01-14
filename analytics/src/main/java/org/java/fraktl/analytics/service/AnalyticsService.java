package org.java.fraktl.analytics.service;

import org.java.fraktl.common.event.UrlEventMessage;

public interface AnalyticsService {

  void record(UrlEventMessage event);

}
