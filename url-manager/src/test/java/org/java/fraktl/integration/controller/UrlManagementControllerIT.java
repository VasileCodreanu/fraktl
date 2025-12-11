package org.java.fraktl.integration.controller;


import static org.java.fraktl.service.impl.helpers.UrlConstants.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java.fraktl.common.api.ApiResponse;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.dto.ShortenUrlRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlManagementControllerIT {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:17"));

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnNotFoundForInvalidShortCode() {
    // Arrange
    String invalidShortCode = "invalid";
    String expectedDebugMessage = String.format(
        "Resource with short-code equal to: '%s' is not present.", invalidShortCode);

    // Act & Assert
    webTestClient.get()
        .uri("/api/v1/short-urls/{shortUrl}", invalidShortCode)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(ApiResponse.ResponseStatus.ERROR)
        .jsonPath("$.data.status").isEqualTo(404)
        .jsonPath("$.data.message").isEqualTo("The requested resource was not found.")
        .jsonPath("$.data.debugMessage").isEqualTo(expectedDebugMessage);
  }

  @Test
  void shouldShortenAndThenExpandShortUrl() throws JsonProcessingException {
    //Arrange
    String originalUrl = "https://example.com";
    ShortenUrlRequest request = new ShortenUrlRequest(originalUrl);

    //Act: Send POST request to shorten the URL
    String apiResponseBody = webTestClient.post()
        .uri("/api/v1/short-urls")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();

    //Assert
    assertNotNull(apiResponseBody);

    ApiResponse<ShortUrlResponse> apiResponse = parse(apiResponseBody, new TypeReference<>() {});
    assertNotNull(apiResponse);

    ApiResponse.ResponseStatus actualStatus = apiResponse.status();
    ApiResponse.ResponseStatus expectedStatus = ApiResponse.ResponseStatus.SUCCESS;
    assertEquals(expectedStatus, actualStatus);

    ShortUrlResponse shortUrlResponse = apiResponse.data();
    assertNotNull(shortUrlResponse);

    String shortUrl = shortUrlResponse.shortUrl();
    assertNotNull(shortUrl);
    assertTrue(shortUrl.startsWith(BASE_URL),
        String.format("Short URL does not start with: %s.", BASE_URL));
    String shortCode = shortUrl.substring(BASE_URL.length());

    //Assert: Retrieve original long URL using the shortcode
    webTestClient.get()
        .uri("/api/v1/short-urls/{shortCode}", shortCode)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo(ApiResponse.ResponseStatus.SUCCESS)
        .jsonPath("$.data.shortCode").isEqualTo(shortCode)
        .jsonPath("$.data.shortUrl").isEqualTo(shortUrl)
        .jsonPath("$.data.originalUrl").isEqualTo(originalUrl);
  }

  public <T> T parse(String json, TypeReference<T> typeRef) throws JsonProcessingException {
    return objectMapper.readValue(json, typeRef);
  }
}