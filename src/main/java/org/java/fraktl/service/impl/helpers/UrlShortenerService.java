package org.java.fraktl.service.impl.helpers;

import static org.java.fraktl.service.impl.helpers.UrlConstants.BASE_URL;
import static org.java.fraktl.service.impl.helpers.UrlConstants.CHAR_SET;
import static org.java.fraktl.service.impl.helpers.UrlConstants.SHORT_URL_LENGTH;

import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

  public String createShortUrl(Long id) {
    if (id == null || id < 100000000000L) {
      throw new IllegalArgumentException("ID must be greater than 100000000000");
    }

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
