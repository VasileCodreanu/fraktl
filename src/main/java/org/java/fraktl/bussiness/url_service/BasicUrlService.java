package org.java.fraktl.bussiness.url_service;


import lombok.RequiredArgsConstructor;
import org.java.fraktl.bussiness.UrlService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUrlService implements UrlService {

    private final UrlShortenerService shortenerService;
    private final UrlExpanderService expanderService;


    public String shortenUrl(String longUrl) {
        return shortenerService.createShortUrl(longUrl);
    }

    public String expandUrl(String shortUrl) {
        return expanderService.expand(shortUrl);
    }
}