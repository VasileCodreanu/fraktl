package com.fraktl.urlmanagement.repository;

import java.util.Optional;
import com.fraktl.urlmanagement.entity.ShortenedUrl;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<ShortenedUrl, Long> {

  Optional<ShortenedUrl> findByOriginalUrl(String originalUrl);

  Optional<ShortenedUrl> findByShortCode(String shortCode);

}
