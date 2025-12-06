package org.java.fraktl.controller;


import static org.java.fraktl.dto.common.ResponseStatus.SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.service.UrlMappingService;
import org.java.fraktl.dto.common.ApiResponse;
import org.java.fraktl.dto.LongUrlResponse;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.dto.ShortenUrlRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix.v1}/short-urls")
@Validated
public class UrlManagementController {

  private final UrlMappingService urlMappingService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<ShortUrlResponse>> createShortUrl(
      @Valid @RequestBody ShortenUrlRequest shortenUrlRequest) {

    String shortUrl = urlMappingService.createShortUrl(shortenUrlRequest);
    ShortUrlResponse responseBody = new ShortUrlResponse(shortUrl);
    ApiResponse<ShortUrlResponse> apiResponse = new ApiResponse<>(SUCCESS, responseBody);

    return new ResponseEntity<>(apiResponse, CREATED);
  }

  @GetMapping(value = "/{shortUrl}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<LongUrlResponse>> getShortUrlDetails(
      @NotBlank @Size(min = 7, max = 7) @PathVariable("shortUrl") String shortUrl) {

    String longUrl = urlMappingService.resolveShortUrl(shortUrl);
//    ShortUrlResponse response = urlMappingService.getShortUrlDetails(shortCode/shortUrl);


    LongUrlResponse responseBody = new LongUrlResponse(longUrl);
    ApiResponse<LongUrlResponse> apiResponse = new ApiResponse<>(SUCCESS, responseBody);

    return new ResponseEntity<>(apiResponse, OK);
  }
}
