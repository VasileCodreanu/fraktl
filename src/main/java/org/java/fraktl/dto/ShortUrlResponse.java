package org.java.fraktl.dto;

import java.time.LocalDateTime;
import org.java.fraktl.entity.ShortenedUrl;

public record ShortUrlResponse(
    String shortCode,
    String shortUrl,
    String originalUrl,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {

  public static ShortUrlResponse from(ShortenedUrl entity) {
    return new ShortUrlResponse(
        entity.getShortCode(),
        entity.getShortUrl(),
        entity.getOriginalUrl(),
        entity.getCreatedAt(),
        entity.getExpiresAt()
    );
  }

}
