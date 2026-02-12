package com.fraktl.urlmanagement.controller;


import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import com.fraktl.common.api.ApiResponse;
import com.fraktl.urlmanagement.service.UrlMappingService;

import com.fraktl.urlmanagement.dto.ShortUrlResponse;
import com.fraktl.urlmanagement.dto.ShortenUrlRequest;
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

    ShortUrlResponse responseBody = urlMappingService.createShortUrl(shortenUrlRequest);

    ApiResponse<ShortUrlResponse> apiResponse = ApiResponse.success(responseBody);
    return new ResponseEntity<>(apiResponse, CREATED);
  }

  @GetMapping(value = "/{shortCode}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<ShortUrlResponse>> getShortCodeDetails(
      @NotBlank @Size(min = 7, max = 7) @PathVariable("shortCode") String shortCode) {

    ShortUrlResponse responseBody = urlMappingService.getShortUrlDetailsByShortCode(shortCode);

    ApiResponse<ShortUrlResponse> apiResponse = ApiResponse.success(responseBody);
    return new ResponseEntity<>(apiResponse, OK);
  }
}
