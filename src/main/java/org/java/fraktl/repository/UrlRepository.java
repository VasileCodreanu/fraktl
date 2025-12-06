package org.java.fraktl.repository;

import org.java.fraktl.entity.ShortenedUrl;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<ShortenedUrl, Long> {

}
