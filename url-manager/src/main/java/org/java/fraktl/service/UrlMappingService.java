package org.java.fraktl.service;

import jakarta.validation.constraints.NotBlank;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.dto.ShortenUrlRequest;

public interface UrlMappingService {

  ShortUrlResponse createShortUrl(ShortenUrlRequest shortenUrlRequest);

  String resolveShortCode(String shortUrl);

  ShortUrlResponse getShortUrlDetailsByShortCode(@NotBlank String shortUrl);
}
