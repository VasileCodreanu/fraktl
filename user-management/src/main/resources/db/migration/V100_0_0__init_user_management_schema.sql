

CREATE TABLE user_management.users
(
    id               BIGSERIAL PRIMARY KEY,
    user_name        VARCHAR(100) NOT NULL,
    registered_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
