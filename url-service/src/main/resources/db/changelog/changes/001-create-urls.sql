--liquibase formatted sql

--changeset usher:001-create-urls
CREATE TABLE urls (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    original_url TEXT NOT NULL,
    short_code VARCHAR(16) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_urls_short_code UNIQUE (short_code),
    CONSTRAINT chk_urls_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX idx_urls_owner_id ON urls (owner_id);

--rollback DROP TABLE IF EXISTS urls;
