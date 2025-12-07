package org.java.fraktl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import org.java.fraktl.entity.ShortenedUrl;

@Builder
public record ShortUrlResponse(
    String shortCode,
    String shortUrl,
    String originalUrl,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime expiresAt
) {

  public static ShortUrlResponse from(ShortenedUrl entity) {
    return ShortUrlResponse.builder()
        .shortCode(entity.getShortCode())
        .shortUrl(entity.getShortCode())
        .originalUrl(entity.getOriginalUrl())
        .createdAt(entity.getCreatedAt())
        .expiresAt(entity.getExpiresAt())
        .build();
  }

}
