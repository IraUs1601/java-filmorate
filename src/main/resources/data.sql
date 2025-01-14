-- Заполнение таблицы жанров
INSERT INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

-- Заполнение таблицы возрастных рейтингов
INSERT INTO mpa_ratings (mpa_rating_id, rating) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- Пример данных для пользователей
INSERT INTO users (name, email, login, birthday) VALUES
('Alice', 'alice@example.com', 'alice', '1990-01-01'),
('Bob', 'bob@example.com', 'bob', '1985-05-15'),
('Charlie', 'charlie@example.com', 'charlie', '1992-12-31');

-- Пример данных для фильмов
INSERT INTO films (title, description, release_date, duration, mpa_rating_id) VALUES
('Film 1', 'Description 1', '2000-01-01', 120, 1),
('Film 2', 'Description 2', '2010-05-15', 90, 2),
('Film 3', 'Description 3', '2020-12-31', 150, 3);

-- Пример связей фильмов и жанров
INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 4);

-- Пример данных для лайков
INSERT INTO likes (user_id, film_id, like_status) VALUES
(1, 1, true),
(2, 1, true),
(3, 2, false);

-- Пример данных для дружбы
INSERT INTO friendships (user_id_1, user_id_2) VALUES
(1, 2),
(2, 3);