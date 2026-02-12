package com.fraktl.common.event;

import java.time.Instant;
import com.fraktl.common.context.RequestContext;

public final class UrlEventFactory {

  private UrlEventFactory() {
  }

  public static UrlEventMessage click(
      String shortCode,
      RequestContext ctx
  ) {
    return baseEvent(
        UrlEventType.CLICK,
        shortCode,
        ctx
    );
  }

  public static UrlEventMessage created(
      String shortCode,
      RequestContext ctx
  ) {
    return baseEvent(
        UrlEventType.CREATED,
        shortCode,
        ctx
    );
  }
  
  private static UrlEventMessage baseEvent(
      UrlEventType type,
      String shortCode,
      RequestContext ctx
      //Map<String, String> metadata
  ) {
    return new UrlEventMessage(
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