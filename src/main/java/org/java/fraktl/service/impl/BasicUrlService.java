package org.java.fraktl.service.impl;


import lombok.RequiredArgsConstructor;
import org.java.fraktl.repository.UrlRepository;
import org.java.fraktl.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import org.java.fraktl.entity.UrlMapping;
import org.java.fraktl.dto.short_url.ShortenUrlRequest;
import org.java.fraktl.service.UrlService;
import org.java.fraktl.service.impl.helpers.UrlExpanderService;
import org.java.fraktl.service.impl.helpers.UrlShortenerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUrlService implements UrlService {

  private final UrlShortenerService shortenerService;
  private final UrlExpanderService expanderService;

  private final UrlRepository urlRepository;

  @Transactional
  public String shortenUrl(ShortenUrlRequest request) {

    UrlMapping urlMapping = new UrlMapping();

    urlRepository.save(urlMapping);
    String shortUrl = shortenerService.createShortUrl(urlMapping.getId());

    urlMapping.setOriginalUrl(request.originalUrl());
    urlMapping.setShortUrl(shortUrl);

    urlRepository.save(urlMapping);

    return shortUrl;
  }

  @Transactional(readOnly = true)
  public String expandUrl(String shortUrl) {

    long id = expanderService.expand(shortUrl);

    UrlMapping urlMapping = urlRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Resource with short-url equal to: '%s' is not present.", shortUrl)));

    return urlMapping.getOriginalUrl();
  }
}