package com.fraktl.analytics.service;

import com.fraktl.common.event.UrlEventMessage;

public interface AnalyticsService {

  void record(UrlEventMessage event);

}
