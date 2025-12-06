package org.java.fraktl.integration.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.dto.ShortenUrlRequest;
import org.java.fraktl.dto.common.ApiResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UrlRedirectControllerIT {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:17"));

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldRedirectToOriginalUrl() throws JsonProcessingException {
    // Arrange: Create a short URL
    String originalUrl = "https://example.com/test-page";
    ShortenUrlRequest request = new ShortenUrlRequest(originalUrl);

    String apiResponseBody = webTestClient.post()
        .uri("/api/v1/short-urls")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();

    assertNotNull(apiResponseBody);

    ApiResponse<ShortUrlResponse> apiResponse = parse(apiResponseBody, new TypeReference<>() {});
    ShortUrlResponse shortUrlResponse = apiResponse.getData();
    String shortCode = shortUrlResponse.shortCode();

    // Act & Assert: Access the redirect endpoint
    webTestClient.get()
        .uri("/{shortCode}", shortCode)
        .exchange()
        .expectStatus().isFound()
        .expectHeader().exists(HttpHeaders.LOCATION)
        .expectHeader().valueEquals(HttpHeaders.LOCATION, originalUrl);
  }

  @Test
  void shouldReturn404ForNonExistentShortCode() {
    // Arrange
    String invalidShortCode = "invalid";

    // Act & Assert
    webTestClient.get()
        .uri("/{shortCode}", invalidShortCode)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo("FAILURE")
        .jsonPath("$.data.status").isEqualTo(404)
        .jsonPath("$.data.message").isEqualTo("The requested resource was not found.");
  }

  @Test
  void shouldReturn400ForShortCodeTooShort() {
    // Arrange
    String tooShortCode = "abc";

    // Act & Assert
    webTestClient.get()
        .uri("/{shortCode}", tooShortCode)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("FAILURE");
  }

  @Test
  void shouldReturn400ForShortCodeTooLong() {
    // Arrange
    String tooLongCode = "abcdefgh";

    // Act & Assert
    webTestClient.get()
        .uri("/{shortCode}", tooLongCode)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("FAILURE");
  }

  @Test
  void shouldReturn400ForBlankShortCode() {
    // Arrange
    String blankCode = "       ";

    // Act & Assert
    webTestClient.get()
        .uri("/{shortCode}", blankCode)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("FAILURE");
  }

  @Test
  void shouldRedirectMultipleTimesToSameUrl() throws JsonProcessingException {
    // Arrange
    String originalUrl = "https://example.com/redirect-test";
    ShortenUrlRequest request = new ShortenUrlRequest(originalUrl);

    String apiResponseBody = webTestClient.post()
        .uri("/api/v1/short-urls")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody();

    ApiResponse<ShortUrlResponse> apiResponse = parse(apiResponseBody, new TypeReference<>() {});
    String shortCode = apiResponse.getData().shortCode();

    // Act & Assert: Redirect multiple times/ensure idempotency
    for (int i = 0; i < 3; i++) {
      webTestClient.get()
          .uri("/{shortCode}", shortCode)
          .exchange()
          .expectStatus().isFound()
          .expectHeader().valueEquals(HttpHeaders.LOCATION, originalUrl);
    }
  }


  private <T> T parse(String json, TypeReference<T> typeRef) throws JsonProcessingException {
    return objectMapper.readValue(json, typeRef);
  }
}