package com.fraktl.usermanagement.service;

import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.BASE_URL;
import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.CHAR_SET;
import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.SHORT_URL_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fraktl.urlmanagement.service.impl.helpers.UrlShortener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class UrlShortenerTest {

  private UrlShortener urlShortener;

  @BeforeEach
  void setUp() {
    urlShortener = new UrlShortener();
  }

  @Test
  void testBase10ToBase62_conversionAccuracy() {
    //Arrange
    long id = 1_000_000_000_000L;

    //Act
    String shortUrl = urlShortener.createShortUrl(id);

    //Assert
    assertNotNull(shortUrl);
    assertTrue(shortUrl.startsWith(BASE_URL));
    assertEquals(BASE_URL.length() + SHORT_URL_LENGTH, shortUrl.length());
    assertEquals(BASE_URL + "hBxM5A4", shortUrl);

    String shortCode = shortUrl.substring(BASE_URL.length());
    assertTrue(shortCode.chars().allMatch(c -> CHAR_SET.indexOf(c) != -1),
        "'shortCode' code contains invalid characters not present in the UrlConstants.CHAR_SET;");
  }

  @Test
  void testCreateShortUrl_idTooSmall_throwsException() {
    //Arrange - Act
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      urlShortener.createShortUrl(99999999999L); // Less than threshold
    });

    //Assert
    assertNotNull(exception, "exception should not be null");
    assertEquals("ID must be greater than 100000000000", exception.getMessage());
  }
}