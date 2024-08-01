INSERT INTO user(login_name, email, pwd, enabled, role) VALUES ('publisher1', 'email1@test.com', '$2a$10$Q5bxyPXhHXFc1fRUMCRWR.GbgsXx9aGZdoEoEAz2JFEfckdyUKfOi', TRUE, 'TRADER');
INSERT INTO user(login_name, email, pwd, enabled, role) VALUES ('publisher2', 'email2@test.com', '$2a$10$MUahUza86ErCxtsgpmMBDeR5VtoGHioRdl03/jQmkM/sk6L.Eg28e', TRUE, 'TRADER');
INSERT INTO user(login_name, email, pwd, enabled, role) VALUES ('user1', 'email3@test.com', '$2a$10$WcgRF8VQ8DKt4h4Hz9pWv.6MXnIRmcPr0j9jqsseprsBwTD4w8WSm', TRUE, 'USER');
INSERT INTO user(login_name, email, pwd, enabled, role) VALUES ('user2', 'email4@test.com', '$2a$10$Q5bxyPXhHXFc1fRUMCRWR.GbgsXx9aGZdoEoEAz2JFEfckdyUKfOi', TRUE, 'USER');
INSERT INTO user(login_name, email, pwd, enabled, role) VALUES ('dgsoloviev.pub@gmail.com', 'email5@test.com', '$2a$10$Q5bxyPXhHXFc1fRUMCRWR.GbgsXx9aGZdoEoEAz2JFEfckdyUKfOi', TRUE, 'USER');

INSERT INTO trader_user(user_id, name) VALUES (1, 'Test Trader 1 ');
INSERT INTO trader_user(user_id, name) VALUES (2, 'Test Trader 2');

INSERT INTO exchange(id, name) VALUE (1, 'binance');
INSERT INTO exchange(id, name) VALUE (2, 'bybit');

INSERT INTO subscription(id, user_id, exchange_id, date) VALUES(1, 1, 1, NOW());
INSERT INTO subscription(id, user_id, exchange_id, date) VALUES(2, 1, 2, NOW());

INSERT INTO session(id, trader_user_id, exchange_id, name, publish_date, uuid) VALUES(1, 1, 1, 'Session1', NOW(), '8305d848-88d2-4cbd-a33b-5c3dcc548056');
INSERT INTO session(id, trader_user_id, exchange_id, name, publish_date, uuid) VALUES(2, 1, 1, 'Session2', NOW(), '09628d25-ea42-490e-965d-cd4ffb6d4e9d');
INSERT INTO session(id, trader_user_id, exchange_id, name, publish_date, uuid) VALUES(3, 2, 2, 'Session3', NOW(), '75f29692-237b-4116-95ed-645de5c57b4d');
