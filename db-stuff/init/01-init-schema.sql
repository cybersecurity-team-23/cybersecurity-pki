CREATE TYPE certificate_type AS ENUM ('ROOT', 'INTERMEDIATE', 'END_ENTITY');
CREATE TYPE request_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    issuer_serial_number BIGINT,
    common_name VARCHAR(255),
    surname VARCHAR(255),
    given_name VARCHAR(255),
    organisation VARCHAR(255),
    organisational_unit VARCHAR(255),
    country CHAR(2),
    email VARCHAR(255),
    type certificate_type,
    status request_status
);
