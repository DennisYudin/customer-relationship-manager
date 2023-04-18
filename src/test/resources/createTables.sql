
DROP TABLE IF EXISTS locations CASCADE;
CREATE TABLE locations (
    location_id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    working_hours VARCHAR(50) NOT NULL,
	type VARCHAR(50) NOT NULL,
	address VARCHAR(100) NOT NULL,
	description VARCHAR(150) NOT NULL,
	capacity_people INT NULL
);

DROP TABLE IF EXISTS events CASCADE;
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    date TIMESTAMP WITH TIME ZONE,
    price INT NOT NULL,
	status VARCHAR(20) NOT NULL,
	description VARCHAR(1500) NOT NULL,
	location_id INT DEFAULT NULL,
    FOREIGN KEY (location_id) REFERENCES locations (location_id) ON DELETE SET DEFAULT
);

DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL
);

DROP TABLE IF EXISTS events_categories CASCADE;
CREATE TABLE events_categories (
    event_id INT,
    category_id  INT,
    PRIMARY KEY (event_id, category_id),
    FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
	surname VARCHAR(120) NOT NULL,
	email VARCHAR(60) NOT NULL,
	login VARCHAR(20) NOT NULL,
	password VARCHAR(120) NOT NULL,
	type VARCHAR(20) NOT NULL
);

DROP TABLE IF EXISTS event_subscriptions CASCADE;
CREATE TABLE event_subscriptions (
    user_id INT,
    event_id  INT,
    PRIMARY KEY (user_id, event_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS tickets CASCADE;
CREATE TABLE tickets (
    ticket_id SERIAL PRIMARY KEY,
	event_name VARCHAR(80) NOT NULL,
    unique_number VARCHAR(10) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
	status VARCHAR(20) NOT NULL,
	user_id INT NOT NULL,
	event_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET DEFAULT,
	FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE SET DEFAULT
);
