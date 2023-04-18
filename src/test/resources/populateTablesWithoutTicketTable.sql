
INSERT INTO categories (category_id, name) VALUES (1, 'exhibition');
INSERT INTO categories (category_id, name) VALUES (2, 'movie');
INSERT INTO categories (category_id, name) VALUES (3, 'theatre');
INSERT INTO categories (category_id, name) VALUES (4, 'Art concert');

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

INSERT INTO event_subscriptions (user_id, event_id) VALUES (2000, 1000);
INSERT INTO event_subscriptions (user_id, event_id) VALUES (2000, 1001);
INSERT INTO event_subscriptions (user_id, event_id) VALUES (2001, 1000);
