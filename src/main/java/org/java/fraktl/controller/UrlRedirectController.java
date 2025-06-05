package org.java.fraktl.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.bussiness.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlRedirectController {

  private final UrlService urlService;

  @GetMapping(value = "/{shortUrl}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> expandShortUrl(@NotBlank @Size(min=7, max=7) @PathVariable String shortUrl) {
    String longUrl = urlService.expandUrl(shortUrl);

    return ResponseEntity.status(302)
        .header("Location", longUrl)
        .build();
  }
}
