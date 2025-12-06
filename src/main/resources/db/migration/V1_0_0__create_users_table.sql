CREATE TABLE shortened_urls
(
    id            BIGINT PRIMARY KEY,
    short_url     VARCHAR(50) UNIQUE,
    original_url  VARCHAR(1000),
    created_at    TIMESTAMP WITH TIME ZONE,
    expiration_at TIMESTAMPTZ
);

DROP SEQUENCE IF EXISTS shortened_urls_sequence;
CREATE SEQUENCE shortened_urls_sequence START 1000000000000;