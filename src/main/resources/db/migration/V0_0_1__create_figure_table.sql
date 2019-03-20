CREATE TABLE figure (
    id BIGSERIAL PRIMARY KEY,
    verbatim VARCHAR(255),
    product_line VARCHAR(255),
    year_released SMALLINT,
    placement_no VARCHAR(32),
    image VARCHAR(255)
);