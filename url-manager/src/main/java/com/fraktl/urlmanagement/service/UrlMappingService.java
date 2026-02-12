package com.fraktl.urlmanagement.service;

import jakarta.validation.constraints.NotBlank;
import com.fraktl.urlmanagement.dto.ShortUrlResponse;
import com.fraktl.urlmanagement.dto.ShortenUrlRequest;

public interface UrlMappingService {

  ShortUrlResponse createShortUrl(ShortenUrlRequest shortenUrlRequest);

  String resolveShortCode(String shortUrl);

  ShortUrlResponse getShortUrlDetailsByShortCode(@NotBlank String shortUrl);
}
