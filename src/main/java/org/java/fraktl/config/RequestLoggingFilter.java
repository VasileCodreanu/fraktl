package org.java.fraktl.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;
  private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
  private static final String X_CORRELATION_ID = "X-Correlation-Id";

  public RequestLoggingFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = Optional
        .ofNullable(request.getHeader(X_CORRELATION_ID))
        .orElse(UUID.randomUUID().toString());

    MDC.put(X_CORRELATION_ID, correlationId);
    response.setHeader(X_CORRELATION_ID, correlationId);

    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();
    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      logRequestDetails(wrappedRequest, wrappedResponse, duration);
      wrappedResponse.copyBodyToResponse();
      MDC.clear();
    }
  }

  private void logRequestDetails(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
    String uri   = request.getRequestURI();
    String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
    int status   = response.getStatus();

    //Request map
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("method", request.getMethod());
    requestMap.put("url", uri + query);
    requestMap.put("clientIp", getClientIpAddress(request));
    requestMap.put("userAgent", request.getHeader("User-Agent"));
    requestMap.put("httpVersion", request.getProtocol());
    requestMap.put("headers", extractRelevantHeaders(request));
    requestMap.put("body", extractBody(request.getContentAsByteArray(), request.getContentType()));

    //Response map
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("status", status);
    responseMap.put("body", extractBody(response.getContentAsByteArray(), response.getContentType()));

    //Root log entry
    Map<String, Object> logEntry = new HashMap<>();
    logEntry.put("logLevel", determineLogLevel(status));
    logEntry.put("durationMs", duration);
    logEntry.put("request", requestMap);
    logEntry.put("response", responseMap);

    try {
      String logJson = objectMapper.writeValueAsString(logEntry);
      switch (logEntry.get("logLevel").toString()) {
        case "ERROR" -> log.error(logJson);
        case "WARN"  -> log.warn(logJson);
        default      -> log.info("HTTP transaction log", StructuredArguments.entries(logEntry));
      }
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize structured log entry", e);
    }
  }

  private String determineLogLevel(int status) {
    if (status >= 500) {
      return "ERROR";
    } else if (status >= 400) {
      return "WARN";
    } else {
      return "INFO";
    }
  }

  private Map<String, String> extractRelevantHeaders(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement().toLowerCase();

      if (List.of("accept", "content-type", "user-agent", "host", "accept-encoding" ).contains(name)) {
        headers.put(name, request.getHeader(name));
      }
    }
    return headers;
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String header = request.getHeader("X-Forwarded-For");
    if (header != null && !header.isEmpty()) {
      return header.split(",")[0]; // In case of multiple IPs, use the first
    }
    return request.getRemoteAddr();
  }

  private Object extractBody(byte[] content, String contentType) {
    if (content.length > 0 && content.length < 2048) {
      if (contentType != null && contentType.contains("json")) {
        try {
          return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
          return "[Invalid JSON Body]";
        }
      } else if (contentType != null && contentType.contains("text")) {
        return new String(content, StandardCharsets.UTF_8);
      }
    } else if (content.length >= 2048) {
      return "[Body content too large or non-text content]";
    }
    return null;
  }
}