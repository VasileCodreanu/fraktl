package org.java.fraktl.common.ports.out;

import org.java.fraktl.common.event.UrlEventMessage;

public interface AnalyticsEventPublisherPort {

  void publishUrlEvent(UrlEventMessage event);

}
