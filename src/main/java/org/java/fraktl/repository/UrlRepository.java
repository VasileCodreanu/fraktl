package org.java.fraktl.repository;

import org.java.fraktl.entity.UrlMapping;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<UrlMapping, Long> {

}
