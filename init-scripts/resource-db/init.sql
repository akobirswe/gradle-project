DROP TABLE IF EXISTS resources;

CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    data BYTEA NOT NULL
);
