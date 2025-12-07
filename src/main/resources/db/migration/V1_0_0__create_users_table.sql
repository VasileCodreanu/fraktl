
CREATE SCHEMA IF NOT EXISTS url_management;

DROP SEQUENCE IF EXISTS url_management.shortened_urls_sequence;
CREATE SEQUENCE url_management.shortened_urls_sequence
    START 1000000000000
    INCREMENT BY 5;

CREATE TABLE url_management.shortened_urls
(
    id            BIGINT PRIMARY KEY DEFAULT nextval('url_management.shortened_urls_sequence'),
    version       BIGINT NOT NULL DEFAULT 0,

    short_code    VARCHAR(8) UNIQUE,
    short_url     VARCHAR(255) UNIQUE,
    original_url  VARCHAR(2048) NOT NULL,

    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at    TIMESTAMPTZ,

    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);


-- Fast redirect by short code
CREATE INDEX IF NOT EXISTS idx_short_code
    ON url_management.shortened_urls(short_code);

-- Only ACTIVE URL codes are queried during redirect
CREATE INDEX IF NOT EXISTS idx_active_shortcodes
    ON url_management.shortened_urls(short_code)
    WHERE is_active = TRUE;

-- Optional: Improve analytics lookups on original URLs
CREATE INDEX IF NOT EXISTS idx_original_url
    ON url_management.shortened_urls(original_url);