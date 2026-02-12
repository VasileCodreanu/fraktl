package com.fraktl.urlmanagement.service.impl.helpers;


import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.BASE_URL;
import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.CHAR_SET;

public class UrlExpander {

  public long expand(String shortUrl) {
    String code = shortUrl.replace(BASE_URL, "");
    return base62ToBase10(code);
  }

  private long base62ToBase10(String encoded) {
    long number = 0;
    for (int i = 0; i < encoded.length(); i++) {
      number = number * CHAR_SET.length() + convert(encoded.charAt(i));
    }
    return number;
  }

  private int convert(char c) {
    if (c >= '0' && c <= '9') {
      return c - '0';
    }
    if (c >= 'a' && c <= 'z') {
      return c - 'a' + 10;
    }
    if (c >= 'A' && c <= 'Z') {
      return c - 'A' + 36;
    }
    return -1;
  }
}
