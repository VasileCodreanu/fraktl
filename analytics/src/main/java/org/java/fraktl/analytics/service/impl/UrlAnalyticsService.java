package org.java.fraktl.analytics.service.impl;

import lombok.RequiredArgsConstructor;
import org.java.fraktl.analytics.helper.UrlEventConverter;
import org.java.fraktl.analytics.repository.UrlEventRepository;
import org.java.fraktl.analytics.service.AnalyticsService;
import org.java.fraktl.common.event.UrlEventMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlAnalyticsService implements AnalyticsService {

  private final UrlEventRepository repository;
  private final UrlEventConverter converter;

  @Transactional
  @Override
  public void record(UrlEventMessage eventMessage) {
    repository.save(converter.toEntity(eventMessage));
  }

}
