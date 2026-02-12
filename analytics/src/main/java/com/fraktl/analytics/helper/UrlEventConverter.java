package com.fraktl.analytics.helper;

import com.fraktl.analytics.entity.UrlEvent;
import com.fraktl.common.event.UrlEventMessage;

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
