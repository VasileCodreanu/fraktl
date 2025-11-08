package org.java.fraktl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
  private static final String CORRELATION_ID = "correlation.id";
  private static final String HTTP = "http";
  private static final String CLIENT = "client";
  private static final String EVENT = "event";
  private static final String USER_ID = "user.id";

  private final ObjectMapper objectMapper;
  private final String apiPrefix;

  public RequestLoggingFilter(
          ObjectMapper objectMapper,
          @Value("${api.prefix.v1}")String apiPrefix) {
    this.objectMapper = objectMapper;
    this.apiPrefix = apiPrefix;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String correlationId = Optional.ofNullable(
            request.getHeader("X-Correlation-Id"))
            .orElse(UUID.randomUUID().toString());

    MDC.put(CORRELATION_ID, correlationId);

    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();
    Throwable exception = null;

    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } catch (Exception e) {
      exception = e;
      throw e;
    } finally {
      long durationMs = System.currentTimeMillis() - startTime;

      try {
        logRequestDetails(wrappedRequest, wrappedResponse, durationMs, exception);
      } catch (Exception e) {
        log.error("Failed to log request details", e);
      }

      wrappedResponse.copyBodyToResponse();
      MDC.clear();
    }
  }

  private void logRequestDetails(
          ContentCachingRequestWrapper request,
          ContentCachingResponseWrapper response,
          long durationMs,
          Throwable exception) {
    String uri = request.getRequestURI();
    String method = request.getMethod();
    int status = response.getStatus();
    String shortUrlsPath = apiPrefix + "/short-urls";

    if (uri.contains("/health") || uri.contains("/actuator")) {
      return; // Skip logging
    }

    boolean isCreateAction = false;
    boolean isGetAction = false;
    boolean isRedirectAction = false;
    if (HttpMethod.GET.matches(method)) {
      if (!uri.startsWith(apiPrefix)) {
        isRedirectAction = true;
      } else if (uri.contains(shortUrlsPath)) {
        isGetAction = true;
      }
    }else if (uri.contains(shortUrlsPath) && HttpMethod.POST.matches(method)) {
      isCreateAction = true;
    }

    Map<String, Object> event = new LinkedHashMap<>();
    event.put("category", "web");
    event.put("type", isCreateAction ? "creation" : "access");
    event.put("action", determineAction(request, shortUrlsPath));
    event.put("outcome", exception != null ? "failure" : (status < 400 ? "success" : "failure"));
    event.put("duration", durationMs);

    Map<String, Object> client = new LinkedHashMap<>();
    client.put("ip", getClientIpAddress(request));
    client.put("user_agent", request.getHeader("User-Agent"));

    String shortCode = isGetAction || isRedirectAction ? (uri.substring(uri.lastIndexOf('/') + 1)) : "N/A";
    String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
    Map<String, Object> http = buildHttpDetails(request, response, uri, query, shortCode, status);

    log.atLevel(determineLevel(status))
              .setMessage(
                      isCreateAction ? "Short URL created" :
                              isGetAction ? "Short URL get" :
                                      isRedirectAction ? "Short URL redirected" :
                                              "HTTP request processed")
              .addKeyValue(EVENT, event)
              .addKeyValue(CLIENT, client)
              .addKeyValue(USER_ID, "user21")
              .addKeyValue(HTTP, http)
              .log();
  }

  private Map<String, Object> buildHttpDetails(
          ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
          String uri, String query, String shortCode, int status) {

    Map<String, Object> requestMap = new LinkedHashMap<>();
    requestMap.put("method", request.getMethod());
    requestMap.put("referrer", request.getHeader("Referer"));
    requestMap.put("url", uri + query);
    requestMap.put("short_code", shortCode);

    Map<String, Object> requestBody = new LinkedHashMap<>();
    String method = request.getMethod();
    boolean isGetMethod = HttpMethod.GET.matches(method);
    boolean shouldLogRequestBody = isGetMethod;
    if (shouldLogRequestBody) {
      requestBody.put("bytes", 0);
      requestBody.put("content", "[Body skipped]");
    } else {
      requestBody.put("bytes", request.getContentAsByteArray().length);
      requestBody.put("content", extractBody(request.getContentAsByteArray(), request.getContentType()));
    }
    requestMap.put("body", requestBody);

    Map<String, Object> responseMap = new LinkedHashMap<>();
    responseMap.put("status_code", status);
    Map<String, Object> responseBody = new LinkedHashMap<>();
    boolean shouldLogResponseBody = !uri.startsWith(apiPrefix) && isGetMethod;
    if (shouldLogResponseBody) {
      responseBody.put("bytes", 0);
      responseBody.put("content", "[Body skipped]");
    } else {
      responseBody.put("bytes", response.getContentAsByteArray().length);
      responseBody.put("content", extractBody(response.getContentAsByteArray(), response.getContentType()));
    }
    responseMap.put("body", responseBody);

    Map<String, Object> http = new LinkedHashMap<>();
    http.put("version", request.getProtocol());
    http.put("request", requestMap);
    http.put("response", responseMap);
    return http;
  }

  private org.slf4j.event.Level determineLevel(int status) {
    if (status >= 500) return org.slf4j.event.Level.ERROR;
    if (status >= 400) return org.slf4j.event.Level.WARN;
    return org.slf4j.event.Level.INFO;
  }

  private String getClientIpAddress(HttpServletRequest request) {
    // Check proxy headers in order
    String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP", // Cloudflare
            "True-Client-IP"
    };

    for (String header : headers) {
      String ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        return ip.split(",")[0].trim();
      }
    }

    return request.getRemoteAddr();
  }

  private Object extractBody(byte[] content, String contentType) {
    if (content.length == 0) return null;
    if (content.length > 2048) return "[Body too large]";
    try {
      String type = contentType == null ? "" : contentType.toLowerCase();
      if (type.contains("json")) {
        return objectMapper.readValue(content, Map.class);
      } else if (type.contains("text")) {
        return new String(content, StandardCharsets.UTF_8);
      } else if (type.startsWith("multipart/")) {
        return "[Multipart omitted]";
      }
    } catch (Exception e) {
      return "[Invalid body]";
    }
    return null;
  }

  private String determineAction(HttpServletRequest request, String shortUrlsPath) {
    String uri = request.getRequestURI();
    String method = request.getMethod();

    if (uri.startsWith(shortUrlsPath)) {
      if (HttpMethod.POST.matches(method)) return "short_url_create";
      if (HttpMethod.GET.matches(method)) return "short_url_get";
    }
    if (HttpMethod.GET.matches(method) && uri.length() == 8) {
      return "short_url_redirect";
    }
    return "http_request";
  }
}