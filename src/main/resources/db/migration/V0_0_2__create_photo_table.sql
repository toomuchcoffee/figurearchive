CREATE TABLE photo (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT,
    url_1280 VARCHAR(255),
    url_500 VARCHAR(255),
    url_400 VARCHAR(255),
    url_250 VARCHAR(255),
    url_100 VARCHAR(255),
    url_75 VARCHAR(255),
    tags TEXT
);