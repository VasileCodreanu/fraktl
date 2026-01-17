package org.java.fraktl.analytics.helper;

import org.java.fraktl.analytics.entity.UrlEvent;
import org.java.fraktl.common.event.UrlEventMessage;

public class UrlEventConverter {

    public UrlEvent toEntity(UrlEventMessage event) {
      return new UrlEvent(
          event.type().name(),
          event.shortCode(),
          event.userId(),
          event.ip(),
          event.userAgent(),
          event.referrer(),
          event.occurredAt()
      );
    }

}
