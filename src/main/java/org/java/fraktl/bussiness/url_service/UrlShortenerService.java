package org.java.fraktl.bussiness.url_service;

import static org.java.fraktl.bussiness.url_service.UrlConstants.BASE_URL;
import static org.java.fraktl.bussiness.url_service.UrlConstants.CHAR_SET;
import static org.java.fraktl.bussiness.url_service.UrlConstants.SHORT_URL_LENGTH;

import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

  public String createShortUrl(Long id) {
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
