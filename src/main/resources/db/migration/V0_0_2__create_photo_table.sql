CREATE TABLE photo (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT,
    urls JSON,
    tags TEXT[]
);