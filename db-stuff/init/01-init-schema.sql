CREATE TYPE request_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    common_name VARCHAR(255),
    organisational_unit VARCHAR(255),
    organisation VARCHAR(255),
    location VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255),
    status VARCHAR(255),
    is_deleted boolean
);
