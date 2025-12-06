package org.java.fraktl.service;

import org.java.fraktl.dto.short_url.ShortenUrlRequest;

public interface UrlService {

  String shortenUrl(ShortenUrlRequest shortenUrlRequest);

  String expandUrl(String shortUrl);
}
