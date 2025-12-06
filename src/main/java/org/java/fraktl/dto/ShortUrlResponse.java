package org.java.fraktl.dto;

import java.time.LocalDateTime;

public record ShortUrlResponse(
    String shortUrl,
    LocalDateTime creationDate,
    LocalDateTime expirationDate
) {

  public ShortUrlResponse(String shortUrl) {
    this(shortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
  }
}
