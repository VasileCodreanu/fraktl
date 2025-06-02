package org.java.fraktl.bussiness;

import org.java.fraktl.model.response.short_url.ShortenUrlRequest;

public interface UrlService {
  String shortenUrl(ShortenUrlRequest shortenUrlRequest);
  String expandUrl(String shortUrl);
}
