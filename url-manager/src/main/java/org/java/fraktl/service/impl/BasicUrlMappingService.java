package org.java.fraktl.service.impl;


import static org.java.fraktl.service.impl.helpers.UrlConstants.BASE_URL;

import lombok.RequiredArgsConstructor;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.repository.UrlRepository;
import org.java.fraktl.common.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import org.java.fraktl.entity.ShortenedUrl;
import org.java.fraktl.dto.ShortenUrlRequest;
import org.java.fraktl.service.UrlMappingService;
import org.java.fraktl.service.impl.helpers.UrlShortenerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUrlMappingService implements UrlMappingService {

  private final UrlShortenerService shortenerService;
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