package com.fraktl.usermanagement.service;

import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fraktl.urlmanagement.service.impl.helpers.UrlExpander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("unit")
class UrlExpanderTest {

  private UrlExpander urlExpander;

  @BeforeEach
  void setUp() {
    urlExpander = new UrlExpander();
  }

  @ParameterizedTest
  @CsvSource({
      "b9, 691",   // 'b9' -> (11 * 62^1) + 9
      "10, 62",    // '10' -> (1  * 62^1) + 0
      "aZ, 681"    // 'aZ' -> (10 * 62^1) + 61
  })
  void testExpand_withValidShortUrls(String shortCode, long expectedId) {
    //Arrange
    String shortUrl = BASE_URL + shortCode;

    //Act
    long result = urlExpander.expand(shortUrl);

    //Assert
    assertNotEquals(0, result, "Expanded ID should not be zero");
    assertEquals(expectedId, result, "Expanded ID should match expected value");
  }

  @Test
  void testExpand_withValidShortUrls() {
    String shortUrl = BASE_URL + "hBxM5A4";

    long result = urlExpander.expand(shortUrl);

    assertEquals(1_000_000_000_000L, result, "Expanded ID should match expected large value");
  }
}