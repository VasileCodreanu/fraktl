package org.java.fraktl.bussiness;

public interface UrlService {
  String shortenUrl(String longUrl);
  String expandUrl(String shortUrl);
}
