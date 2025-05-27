package org.java.fraktl.bussiness.url_service;

import static org.java.fraktl.bussiness.url_service.UrlConstants.BASE_URL;
import static org.java.fraktl.bussiness.url_service.UrlConstants.CHAR_SET;
import static org.java.fraktl.bussiness.url_service.UrlConstants.SHORT_URL_LENGTH;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class UrlShortenerService {

  private final ConcurrentMap<String, Long> longToShortMap;
  private final ConcurrentMap<Long, String> shortToLongMap;
  private final AtomicLong counter;

  public UrlShortenerService(
      ConcurrentMap<String, Long> longToShortMap,
      ConcurrentMap<Long, String> shortToLongMap,
      AtomicLong counter) {
    this.longToShortMap = longToShortMap;
    this.shortToLongMap = shortToLongMap;
    this.counter = counter;
  }

  public String createShortUrl(String longUrl) {
    long id = counter.getAndIncrement();
    longToShortMap.put(longUrl, id);
    shortToLongMap.put(id, longUrl);
    String shortCode = base10ToBase62(id);
    return BASE_URL + shortCode;
  }

  private String base10ToBase62(long number) {
    StringBuilder sb = new StringBuilder();
    while (number > 0) {
      int remainder = (int) (number % CHAR_SET.length());
      sb.insert(0, CHAR_SET.charAt(remainder));
      number /= CHAR_SET.length();
    }
    while (sb.length() < SHORT_URL_LENGTH) {
      sb.insert(0, '0');
    }
    return sb.toString();
  }
}
