package org.java.fraktl.common.event;

import java.time.Instant;
import java.util.Map;
import org.java.fraktl.common.context.RequestContext;

public final class UrlEventFactory {

  private UrlEventFactory() {
  }

  public static UrlEvent click(
      String shortCode,
      RequestContext ctx
  ) {
    return baseEvent(
        UrlEventType.CLICK,
        shortCode,
        ctx
    );
  }

  public static UrlEvent created(
      String shortCode,
      RequestContext ctx
  ) {
    return baseEvent(
        UrlEventType.CREATED,
        shortCode,
        ctx
    );
  }
  
  private static UrlEvent baseEvent(
      UrlEventType type,
      String shortCode,
      RequestContext ctx
      //Map<String, String> metadata
  ) {
    return new UrlEvent(
//                UUID.randomUUID()
        type,
        shortCode,
        ctx.userId(),
        ctx.ip(),
        ctx.userAgent(),
        ctx.referrer(),
        Instant.now()
    );
  }
}