package org.java.fraktl.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.service.UrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlRedirectController {

  private final UrlMappingService urlMappingService;

  @GetMapping(value = "/{shortUrl}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> redirect(
      @NotBlank @Size(min = 7, max = 7) @PathVariable String shortUrl) {

    String originalUrl = urlMappingService.resolveShortUrl(shortUrl);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(originalUrl))
        .build();
  }
}
