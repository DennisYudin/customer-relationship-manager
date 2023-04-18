
INSERT INTO categories (name) VALUES ('exhibition');
INSERT INTO categories (name) VALUES ('movie');
INSERT INTO categories (name) VALUES ('theatre');
INSERT INTO categories (name) VALUES ('Art concert');

INSERT INTO locations (location_id, name, working_hours, type, address, description, capacity_people)
VALUES (100, 'Drunk oyster', '08:00-22:00', 'bar', 'FooBar street', 'description test', 300);
INSERT INTO locations (location_id, name, working_hours, type, address, description, capacity_people)
VALUES (101, 'Moes', '06:00-00:00', 'tavern', 'the great street', 'description bla bla bla for test', 750);

INSERT INTO events  (event_id, name, date, price, status, description, location_id)
VALUES (1000, 'Oxxxymiron concert', '2021-08-13 18:23:00', 5000, 'actual', 'Oxxxymiron is', 101);
INSERT INTO events  (event_id, name, date, price, status, description, location_id)
VALUES (1001, 'Basta', '2019-09-14 15:30:00', 1000, 'actual', 'Bla bla bla', 100);

INSERT INTO events_categories (event_id, category_id) VALUES (1000, 1);
INSERT INTO events_categories (event_id, category_id) VALUES (1000, 2);
INSERT INTO events_categories (event_id, category_id) VALUES (1000, 3);
INSERT INTO events_categories (event_id, category_id) VALUES (1001, 3);
INSERT INTO events_categories (event_id, category_id) VALUES (1001, 2);

INSERT INTO users (user_id, name, surname, email, login, password, type)
VALUES (2000, 'Dennis', 'Yudin', 'dennisYudin@mail.ru', 'Big boss', '0000', 'customer');
INSERT INTO users (user_id, name, surname, email, login, password, type)
VALUES (2001, 'Mark', 'Batmanov', 'redDragon@mail.ru', 'HelloWorld', '1234', 'customer');

INSERT INTO tickets (ticket_id, event_name, unique_number, creation_date, status, user_id, event_id)
VALUES (3000, 'Oxxxymiron concert', '123456789', '1992-02-17 20:45:00', 'actual', 2000, 1000);
INSERT INTO tickets (ticket_id, event_name, unique_number, creation_date, status, user_id, event_id)
VALUES (3001, 'Basta', '987654321', '1986-02-18 02:30:00', 'cancelled', 2001, 1001);

