package com.fraktl.analytics.repository;

import com.fraktl.analytics.entity.UrlEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlEventRepository extends CrudRepository<UrlEvent, Long> {

}
