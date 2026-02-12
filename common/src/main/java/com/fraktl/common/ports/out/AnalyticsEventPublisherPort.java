package com.fraktl.common.ports.out;

import com.fraktl.common.event.UrlEventMessage;

public interface AnalyticsEventPublisherPort {

  void publishUrlEvent(UrlEventMessage event);

}
