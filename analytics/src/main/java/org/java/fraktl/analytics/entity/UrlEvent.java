package org.java.fraktl.analytics.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(schema = "url_analytics", name = "url_events")
public class UrlEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "short_code", nullable = false)
  private String shortCode;

  private String userId;
  private String ipHash;

  @Column(columnDefinition = "TEXT")
  private String userAgent;

  @Column(columnDefinition = "TEXT")
  private String referrer;

  @Column(nullable = false)
  private Instant occurredAt;

  public UrlEvent(String eventType, String shortCode, String userId, String ipHash,
      String userAgent, String referrer, Instant occurredAt) {
    this.eventType = eventType;
    this.shortCode = shortCode;
    this.userId = userId;
    this.ipHash = ipHash;
    this.userAgent = userAgent;
    this.referrer = referrer;
    this.occurredAt = occurredAt;
  }

}
