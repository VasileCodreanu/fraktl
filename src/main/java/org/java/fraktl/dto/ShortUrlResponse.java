package org.java.fraktl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.Builder;
import org.java.fraktl.entity.ShortenedUrl;

@Builder
public record ShortUrlResponse(
    String shortCode,
    String shortUrl,
    String originalUrl,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant createdAt,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant expiresAt
) {

  public static ShortUrlResponse from(ShortenedUrl entity) {
    return ShortUrlResponse.builder()
        .shortCode(entity.getShortCode())
        .shortUrl(entity.getShortUrl())
        .originalUrl(entity.getOriginalUrl())
        .createdAt(entity.getCreatedAt())
        .expiresAt(entity.getExpiresAt())
        .build();
  }

}
