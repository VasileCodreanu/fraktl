package org.java.fraktl.bussiness.url_service;


import lombok.RequiredArgsConstructor;
import org.java.fraktl.bussiness.UrlService;
import org.java.fraktl.bussiness.repository.UrlRepository;
import org.java.fraktl.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import org.java.fraktl.model.entity.UrlMapping;
import org.java.fraktl.model.response.short_url.ShortenUrlRequest;
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