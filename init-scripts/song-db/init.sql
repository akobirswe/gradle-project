DROP TABLE IF EXISTS songs;

CREATE TABLE songs (
    id       BIGINT       NOT NULL,
    album    VARCHAR(100) NOT NULL,
    artist   VARCHAR(100) NOT NULL,
    duration VARCHAR(255) NOT NULL,
    name     VARCHAR(100) NOT NULL,
    year     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
