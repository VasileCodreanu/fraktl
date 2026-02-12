package com.fraktl.analytics.service.impl;

import lombok.RequiredArgsConstructor;
import com.fraktl.analytics.helper.UrlEventConverter;
import com.fraktl.analytics.repository.UrlEventRepository;
import com.fraktl.analytics.service.AnalyticsService;
import com.fraktl.common.event.UrlEventMessage;
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
