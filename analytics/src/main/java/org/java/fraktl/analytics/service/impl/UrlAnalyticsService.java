package org.java.fraktl.analytics.service.impl;

import lombok.RequiredArgsConstructor;
import org.java.fraktl.analytics.helper.UrlEventMapper;
import org.java.fraktl.analytics.repository.UrlEventRepository;
import org.java.fraktl.analytics.service.AnalyticsService;
import org.java.fraktl.common.event.UrlEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlAnalyticsService implements AnalyticsService {

  private final UrlEventRepository repository;
  private final UrlEventMapper mapper;

  @Override
  public void record(UrlEvent event) {
    repository.save(mapper.toEntity(event));
  }

}
