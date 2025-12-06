package org.java.fraktl.service;

import org.java.fraktl.dto.ShortenUrlRequest;

public interface UrlMappingService {

  String createShortUrl(ShortenUrlRequest shortenUrlRequest);

  String resolveShortUrl(String shortUrl);
}
