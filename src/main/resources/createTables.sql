
DROP TABLE IF EXISTS customer CASCADE;
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(45) DEFAULT NULL,
    last_name VARCHAR(45) DEFAULT NULL,
    email VARCHAR(45) DEFAULT NULL
    );