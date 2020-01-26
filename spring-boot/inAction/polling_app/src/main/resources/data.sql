INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_DBA');



INSERT INTO users (id, created_at, updated_at, email, name, password, username) VALUES (1, '2020-01-25 10:47:34', '2020-01-25 10:47:34', 'Jarvan1110@gmail.com', 'user', '$2y$12$XO3kZlwtgDTFZTFw1Uwp9.qZX3zeBS1OvdNlGQMYzQ.MIGu4o2AzG', 'user');
INSERT INTO users (id, created_at, updated_at, email, name, password, username) VALUES (2, '2020-01-25 10:47:34', '2020-01-25 10:47:34', 'Johnny0374@gmail.com', 'admin', '$2y$12$4Anr0bWNaIK6i6pBZygmWuOw.RlHKzw4bt4N6lVW9VU.zEJTEAcv.', 'admin');
INSERT INTO users (id, created_at, updated_at, email, name, password, username) VALUES (3, '2020-01-25 10:47:34', '2020-01-25 10:47:34', '10646029@gmail.com', 'dba', '$2y$12$kDM8xCaIosuVsO8f1E1gn.qlt0LhbUdVr6NCCgrBtqO0P5eQse5GK', 'dba');



INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);


INSERT INTO polls (id, created_at, updated_at, created_by, updated_by, expiration_date_time, question) VALUES (1, '2020-01-26 16:08:05', '2020-01-26 16:08:06', 1, 1, '2020-01-26 16:08:19', '武漢肺炎嚴重嗎?');
INSERT INTO polls (id, created_at, updated_at, created_by, updated_by, expiration_date_time, question) VALUES (2, '2020-01-26 16:10:51', '2020-01-26 16:10:11', 1, 1, '2020-01-26 16:10:19', '雞腿好吃還是雞好吃?');


INSERT INTO choices (id, text, poll_id) VALUES (1, '嚴重', 1);
INSERT INTO choices (id, text, poll_id) VALUES (2, '一般般', 1);
INSERT INTO choices (id, text, poll_id) VALUES (3, '不嚴重', 1);
INSERT INTO choices (id, text, poll_id) VALUES (5, '雞腿', 2);
INSERT INTO choices (id, text, poll_id) VALUES (6, '雞翅', 2);


INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (1, '2020-01-26 16:11:41', '2020-01-26 16:11:42', 1, 1, 3);
INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (3, '2020-01-26 16:12:09', '2020-01-26 16:12:10', 1, 1, 1);
INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (4, '2020-01-26 16:13:02', '2020-01-26 16:13:02', 2, 1, 2);
INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (5, '2020-01-26 16:13:10', '2020-01-26 16:13:11', 1, 2, 1);
INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (6, '2020-01-26 16:13:24', '2020-01-26 16:13:24', 1, 2, 2);
INSERT INTO votes (id, created_at, updated_at, choice_id, poll_id, user_id) VALUES (7, '2020-01-26 16:13:35', '2020-01-26 16:13:36', 1, 2, 3);