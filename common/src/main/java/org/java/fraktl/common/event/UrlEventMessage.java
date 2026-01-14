package org.java.fraktl.common.event;

import java.time.Instant;

public record UrlEventMessage(

    UrlEventType type,
    String shortCode,
    String userId,
    String ip,
    String userAgent,
    String referrer,
    Instant occurredAt
    //Map<String, String> metadata

) {

}
