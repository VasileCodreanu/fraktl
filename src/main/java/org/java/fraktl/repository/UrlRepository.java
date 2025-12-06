package org.java.fraktl.repository;

import java.util.Optional;
import org.java.fraktl.entity.ShortenedUrl;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<ShortenedUrl, Long> {

  Optional<ShortenedUrl> findByOriginalUrl(String originalUrl);

  Optional<ShortenedUrl> findByShortCode(String shortCode);

}
