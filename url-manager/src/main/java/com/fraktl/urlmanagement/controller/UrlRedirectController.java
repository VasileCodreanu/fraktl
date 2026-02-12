package com.fraktl.urlmanagement.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import com.fraktl.common.context.RequestContext;
import com.fraktl.common.event.UrlEventMessage;
import com.fraktl.common.event.UrlEventFactory;
import com.fraktl.common.ports.out.AnalyticsEventPublisherPort;
import com.fraktl.urlmanagement.service.UrlMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlRedirectController {

  private final UrlMappingService urlMappingService;
  private final AnalyticsEventPublisherPort analyticsEventPublisher;

  @GetMapping(value = "/{shortCode}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> redirect(
      @NotBlank @Size(min = 7, max = 7) @PathVariable("shortCode") String shortCode,
      HttpServletRequest request) {

    String originalUrl = urlMappingService.resolveShortCode(shortCode);

    //TODO: Async later
    RequestContext ctx = RequestContext.from(request);
    UrlEventMessage urlEventMessage = UrlEventFactory.click(shortCode, ctx);
    analyticsEventPublisher.publishUrlEvent(urlEventMessage);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(originalUrl))
        .build();
  }
}
