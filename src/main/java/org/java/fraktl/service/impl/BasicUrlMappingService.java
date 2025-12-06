package org.java.fraktl.service.impl;


import lombok.RequiredArgsConstructor;
import org.java.fraktl.repository.UrlRepository;
import org.java.fraktl.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import org.java.fraktl.entity.ShortenedUrl;
import org.java.fraktl.dto.ShortenUrlRequest;
import org.java.fraktl.service.UrlMappingService;
import org.java.fraktl.service.impl.helpers.UrlExpanderService;
import org.java.fraktl.service.impl.helpers.UrlShortenerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUrlMappingService implements UrlMappingService {

  private final UrlShortenerService shortenerService;
  private final UrlExpanderService expanderService;

  private final UrlRepository urlRepository;

  @Transactional
  public String createShortUrl(ShortenUrlRequest request) {

    ShortenedUrl shortenedUrl = new ShortenedUrl();

    urlRepository.save(shortenedUrl);
    String shortUrl = shortenerService.createShortUrl(shortenedUrl.getId());

    shortenedUrl.setOriginalUrl(request.originalUrl());
    shortenedUrl.setShortUrl(shortUrl);

    urlRepository.save(shortenedUrl);

    return shortUrl;
  }

  @Transactional(readOnly = true)
  public String resolveShortUrl(String shortUrl) {

    long id = expanderService.expand(shortUrl);

    ShortenedUrl shortenedUrl = urlRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Resource with short-url equal to: '%s' is not present.", shortUrl)));

    return shortenedUrl.getOriginalUrl();
  }
}