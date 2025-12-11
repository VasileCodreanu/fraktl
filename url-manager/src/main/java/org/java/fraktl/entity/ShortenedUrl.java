package org.java.fraktl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Table(
    name = "shortened_urls",
    schema = "url_management"
)
@Getter
@Setter
@NoArgsConstructor
public class ShortenedUrl {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
  @SequenceGenerator(
      name = "url_seq",
      sequenceName = "url_management.shortened_urls_sequence",
      allocationSize = 5
  )
  private Long id;

  @Version
  @Column
  private Long version;

  @Column(name = "short_code", unique = true, length = 8)
  private String shortCode;

  @Column(name = "short_url", unique = true, length = 255)
  private String shortUrl;

  @Column(name = "original_url", nullable = false, length = 2048)
  private String originalUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
    if (this.expiresAt == null) {
      this.expiresAt = createdAt.plus(100, ChronoUnit.DAYS);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ShortenedUrl that = (ShortenedUrl) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}