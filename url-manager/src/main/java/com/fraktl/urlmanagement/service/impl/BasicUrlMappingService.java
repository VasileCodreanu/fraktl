package com.fraktl.urlmanagement.service.impl;


import static com.fraktl.urlmanagement.service.impl.helpers.UrlConstants.BASE_URL;

import lombok.RequiredArgsConstructor;
import com.fraktl.urlmanagement.dto.ShortUrlResponse;
import com.fraktl.urlmanagement.repository.UrlRepository;
import com.fraktl.common.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import com.fraktl.urlmanagement.entity.ShortenedUrl;
import com.fraktl.urlmanagement.dto.ShortenUrlRequest;
import com.fraktl.urlmanagement.service.UrlMappingService;
import com.fraktl.urlmanagement.service.impl.helpers.UrlShortener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUrlMappingService implements UrlMappingService {

  private final UrlShortener shortenerService;
  private final UrlRepository urlRepository;

  @Override
  @Transactional
  public ShortUrlResponse createShortUrl(ShortenUrlRequest request) {

    return urlRepository.findByOriginalUrl(request.originalUrl())
        .map(ShortUrlResponse::from)
        .orElseGet(() -> {
          ShortenedUrl entity = persistOriginalUrl(request.originalUrl());
          enrichWithShortUrl(entity);
          return ShortUrlResponse.from(entity);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public String resolveShortCode(String shortCode) {
    return findByShortCode(shortCode).getOriginalUrl();
  }

  @Override
  @Transactional(readOnly = true)
  public ShortUrlResponse getShortUrlDetailsByShortCode(String shortCode) {
    return ShortUrlResponse.from(findByShortCode(shortCode));
  }

  private ShortenedUrl persistOriginalUrl(String originalUrl) {
    ShortenedUrl entity = new ShortenedUrl();
    entity.setOriginalUrl(originalUrl);
    return urlRepository.save(entity);
  }

  private void enrichWithShortUrl(ShortenedUrl entity) {
    String shortUrl = shortenerService.createShortUrl(entity.getId());
    entity.setShortUrl(shortUrl);
    entity.setShortCode(extractShortCode(shortUrl));
  }

  private ShortenedUrl findByShortCode(String shortCode) {
    return urlRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Resource with short-code equal to: '%s' is not present.".formatted(shortCode)));
  }

  private String extractShortCode(String shortUrl) {
    if (shortUrl == null || !shortUrl.startsWith(BASE_URL)) {
      throw new IllegalArgumentException("Invalid short-url format");
    }
    return shortUrl.substring(BASE_URL.length());
  }

}