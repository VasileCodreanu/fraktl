
CREATE TABLE url_mappings (
    id              BIGINT                        PRIMARY KEY,
    short_url       VARCHAR(50)                   UNIQUE,
    original_url    VARCHAR(1000),
    created_at      TIMESTAMP WITH TIME ZONE,
    expiration_at   TIMESTAMPTZ
);

DROP SEQUENCE IF EXISTS url_mapping_sequence;
CREATE SEQUENCE url_mapping_sequence START 1000000000000;