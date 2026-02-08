

CREATE TABLE url_analytics.url_events
(
    id               BIGSERIAL PRIMARY KEY,

    event_type       VARCHAR(10) NOT NULL,
    short_code       VARCHAR(10) NOT NULL,

    user_id          VARCHAR(128),
    ip_hash          VARCHAR(128),

    user_agent       TEXT,
    referrer         TEXT,

    occurred_at      TIMESTAMPTZ NOT NULL
);



-- When 'short_code' are queried during redirect
CREATE INDEX IF NOT EXISTS idx_short_code
    ON url_management.shortened_urls(short_code);

-- When 'active short_code' are queried during redirect
CREATE INDEX IF NOT EXISTS idx_active_shortcodes
    ON url_management.shortened_urls(short_code)
    WHERE is_active = TRUE;

-- Improve lookups on original URLs
CREATE INDEX IF NOT EXISTS idx_original_url
    ON url_management.shortened_urls(original_url);