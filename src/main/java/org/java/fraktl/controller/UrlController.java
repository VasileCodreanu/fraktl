package org.java.fraktl.controller;


import static org.java.fraktl.model.response.ResponseStatus.SUCCESS;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.bussiness.UrlService;
import org.java.fraktl.model.response.ApiResponse;
import org.java.fraktl.model.response.long_url.LongUrlResponse;
import org.java.fraktl.model.response.short_url.ShortUrlResponse;
import org.java.fraktl.model.response.short_url.ShortenUrlRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix.v1}/short-urls")
public class UrlController {

  private final UrlService urlService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest shortenUrlRequest) {
    String shortUrl = urlService.shortenUrl(shortenUrlRequest);

    return new ResponseEntity<>(new ApiResponse(SUCCESS, new ShortUrlResponse(shortUrl)), FOUND);
  }

  @GetMapping(value = "/{shortUrl}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse> expandShortUrl(@NotBlank @Size(min=7, max=7) @PathVariable String shortUrl) {
    String longUrl = urlService.expandUrl(shortUrl);

    return new ResponseEntity<>(new ApiResponse(SUCCESS, new LongUrlResponse(longUrl)), OK);
  }
}
