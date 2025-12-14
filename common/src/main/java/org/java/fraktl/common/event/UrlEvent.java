package org.java.fraktl.common.event;

import java.time.Instant;

public record UrlEvent(

    UrlEventType type,
    String shortUrl,
    String userId,
    String ip,
    String userAgent,
    String referrer,
    Instant occurredAt
    //Map<String, String> metadata

) {

}
