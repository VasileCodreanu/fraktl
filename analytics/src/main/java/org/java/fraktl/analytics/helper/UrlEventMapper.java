package org.java.fraktl.analytics.helper;

import org.java.fraktl.analytics.entity.UrlEventEntity;
import org.java.fraktl.common.event.UrlEvent;
import org.springframework.stereotype.Component;

@Component
public class UrlEventMapper {

    public UrlEventEntity toEntity(UrlEvent event) {
      return new UrlEventEntity(
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
