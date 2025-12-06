package org.java.fraktl.service.impl;


import static org.java.fraktl.service.impl.helpers.UrlConstants.BASE_URL;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.dto.ShortUrlResponse;
import org.java.fraktl.repository.UrlRepository;
import org.java.fraktl.exceptions.errorModel.customExceptions.ResourceNotFoundException;
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

    Optional<ShortenedUrl> existingShortenedUrl = urlRepository.findByOriginalUrl(request.originalUrl());
    if (existingShortenedUrl.isPresent()) {
      return ShortUrlResponse.from(existingShortenedUrl.get());
    }

    ShortenedUrl entity = new ShortenedUrl();
    entity.setOriginalUrl(request.originalUrl());

    urlRepository.save(entity);

    String shortUrl = shortenerService.createShortUrl(entity.getId());

    entity.setShortUrl(shortUrl);
    entity.setShortCode(extractShortCode(shortUrl));

    return ShortUrlResponse.from(urlRepository.save(entity));

  }

  @Override
  @Transactional(readOnly = true)
  public String resolveShortCode(String shortCode) {

    ShortenedUrl shortenedUrl = urlRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Resource with short-code equal to: '%s' is not present.", shortCode)));

    return shortenedUrl.getOriginalUrl();
  }

  @Override
  @Transactional(readOnly = true)
  public ShortUrlResponse getShortUrlDetailsByShortCode(String shortCode) {

    ShortenedUrl shortenedUrl = urlRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Resource with short-code equal to: '%s' is not present.", shortCode)));

    return ShortUrlResponse.from(shortenedUrl);
  }

  private String extractShortCode(String shortUrl) {
    if (shortUrl == null || !shortUrl.startsWith(BASE_URL)) {
      throw new IllegalArgumentException("Invalid short-url format");
    }
    return shortUrl.substring(BASE_URL.length());
  }

}