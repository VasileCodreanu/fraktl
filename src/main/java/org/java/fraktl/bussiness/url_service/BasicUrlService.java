package org.java.fraktl.bussiness.url_service;


import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.bussiness.UrlService;
import org.java.fraktl.bussiness.repository.UrlRepository;
import org.java.fraktl.model.entity.UrlMapping;
import org.java.fraktl.model.response.short_url.ShortenUrlRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUrlService implements UrlService {

    private final UrlShortenerService shortenerService;
    private final UrlExpanderService expanderService;

    private final UrlRepository urlRepository;
    private final AtomicLong counter;

    public String shortenUrl(ShortenUrlRequest request) {
        String shortUrl = shortenerService.createShortUrl();

        //dto to entity mapping
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(counter.get() - 1);
        urlMapping.setOriginalUrl(request.originalUrl());
        urlMapping.setShortUrl(shortUrl);

        //save to repo
        UrlMapping saveMapping = urlRepository.save(urlMapping);

        return saveMapping.getShortUrl();
    }

    public String expandUrl(String shortUrl) {

        long id = expanderService.expand(shortUrl);

        //get from DB long url by id
        UrlMapping urlMapping = urlRepository.findById(id).get();

        return urlMapping.getOriginalUrl();
    }
}