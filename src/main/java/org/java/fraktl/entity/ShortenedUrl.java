package org.java.fraktl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Table(name = "shortened_urls")
@Getter
@Setter
@NoArgsConstructor
public class ShortenedUrl {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
  @SequenceGenerator(
      name = "url_seq",
      sequenceName = "shortened_urls_sequence",
      allocationSize = 1
  )
  private Long id;

  @Column(name = "short_code", nullable = false, unique = true)
  private String shortCode;

  @Column(name = "short_url", nullable = false, unique = true)
  private String shortUrl;

  @Column(name = "original_url", nullable = false)
  private String originalUrl;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.expiresAt = createdAt.plusDays(100);
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