INSERT INTO `users` (id, email, is_moderator , name, password, reg_time)
VALUES
(1, "vittaminka89@mail.ru", 1, "Маша С", "$2y$12$EQF24xrou2NNWHaXSr8bie0mHoqKraeTobxjMaxN/scEStrAK44.u", '2021-01-20 19:04:18'),
(2, "u2@mail.ru", 0, "Илья П", "$2y$12$EQF24xrou2NNWHaXSr8bie0mHoqKraeTobxjMaxN/scEStrAK44.u", '2021-02-21 18:04:18'),
(3, "u3@mail.ru", 0, "Дима С", "$2y$12$EQF24xrou2NNWHaXSr8bie0mHoqKraeTobxjMaxN/scEStrAK44.u", '2021-03-22 11:04:18'),
(4, "u4@mail.ru", 0, "Соня У", "$2y$12$EQF24xrou2NNWHaXSr8bie0mHoqKraeTobxjMaxN/scEStrAK44.u", '2021-04-2 9:04:18'),
(5, "u5@mail.ru", 0, "Алина Ч", "11111", '2021-04-3 8:04:18');

INSERT INTO `posts` (id, is_active, moderation_status , text, time, title, view_count, user_id)
VALUES
(1, 1, 'ACCEPTED', "hello world from Masha", '2021-01-20 20:04:18', "hey hey", 0, 1),
(2, 1, 'ACCEPTED', "wats up", '2021-01-22 20:04:18', "bon jour", 0, 1),
(3, 1, 'ACCEPTED', "hello world from Илья", '2021-02-23 21:04:18', "guten morgen", 0, 2),
(4, 1, 'ACCEPTED', "hello world from Дима", '2021-03-23 22:04:18', "oi", 0, 3),
(5, 1, 'ACCEPTED', "very very long post about whatever very very long post about whatever  very very long post about whatever  very very long post about whatever trololotrololotrololo", '2021-04-3 20:04:18', "buenas dias", 1, 4),
(6, 1, 'ACCEPTED', "hello world from Алина", '2021-04-3 20:04:18', "ola", 0, 5),
(7, 1, 'ACCEPTED', "lets talk guys", '2021-04-4 20:04:18', "attention", 0, 5),
(8, 1, 'ACCEPTED', "lets talk guys", '2021-04-5 20:04:18', "attention", 0, 5),
(9, 1, 'ACCEPTED', "lets talk guys", '2021-04-6 20:04:18', "attention", 0, 5),
(10, 1, 'ACCEPTED', "lets talk guys", '2021-04-7 20:04:18', "attention", 0, 5),
(11, 1, 'ACCEPTED', "lets talk guys", '2021-04-8 20:04:18', "attention", 0, 5),
(12, 1, 'ACCEPTED', "lets talk guys", '2021-04-9 20:04:18', "attention", 0, 5),
(13, 1, 'ACCEPTED', "lets talk guys", '2021-04-10 20:04:18', "attention", 0, 4),
(14, 1, 'ACCEPTED', "future post", '2021-06-11 20:04:18', "attention", 0, 3),
(15, 1, 'NEW', "new post", '2021-04-11 20:04:18', "attention", 0, 2),
(16, 0, 'ACCEPTED', "not active post", '2021-04-11 20:04:18', "attention", 0, 4);

INSERT INTO `post_comments` (id, text, time, post_id, user_id)
VALUES
(1, "nice to see you here", '2021-02-22 20:04:18', 1, 2),
(2, "hi", '2021-02-24 21:04:18', 3, 1),
(3, "а где это?", '2021-04-10 21:04:18', 1, 5),
(4, "wats up?", '2021-04-11 21:04:18', 7, 4),
(5, "que paso?", '2021-04-10 21:04:18', 7, 3);

INSERT INTO `post_votes` (id, time, value, post_id, user_id)
VALUES
(1, '2021-04-12 20:04:19', 1,  1, 2),
(2, '2021-04-12 20:05:19', 1,  1, 5),
(3, '2021-04-12 20:06:19', 1,  7, 1),
(4, '2021-04-12 20:07:19', 1,  7, 2),
(5, '2021-04-12 20:08:19', 1,  7, 3),
(6, '2021-04-12 20:09:19', 0,  13, 4),
(7, '2021-04-12 20:10:19', 0,  13, 5),
(8, '2021-04-12 20:11:19', 0,  12, 1),
(9, '2021-04-12 20:12:19', 0,  7, 3),
(10, '2021-04-12 20:12:19', 0,  7, 4),
(11, '2021-04-12 20:14:19', 0,  7, 5),
(12, '2021-04-12 20:15:19', 0,  10, 1),
(13, '2021-04-12 20:16:19', 0,  9, 1);

INSERT INTO `tags` (id, name)
VALUES
(1, "lol"),
(2, "xoxo"),
(3, "ddd"),
(4, "sos"),
(5, "oioi"),
(6, "dddhey"),
(7, "dddoi");

INSERT INTO `tag2post` (id, post_id, tag_id)
VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 3),
(4, 7, 4),
(5, 7, 5),
(6, 8, 1),
(7, 8, 5);

