-- Удаление существующих таблиц
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa_ratings;

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    login VARCHAR(255) UNIQUE NOT NULL,
    birthday DATE NOT NULL
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genres (
    genre_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Создание таблицы возрастных рейтингов
CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_rating_id INT PRIMARY KEY,
    rating VARCHAR(50) NOT NULL
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_rating_id INT NOT NULL,
    CONSTRAINT fk_mpa_rating FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings(mpa_rating_id)
);

-- Создание таблицы связей фильмов и жанров
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genres FOREIGN KEY (film_id) REFERENCES films(film_id),
    CONSTRAINT fk_genre_genres FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

-- Создание таблицы лайков
CREATE TABLE IF NOT EXISTS likes (
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    like_status BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_user_likes FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_film_likes FOREIGN KEY (film_id) REFERENCES films(film_id)
);

-- Создание таблицы дружбы
CREATE TABLE IF NOT EXISTS friendships (
    user_id_1 INT NOT NULL,
    user_id_2 INT NOT NULL,
    PRIMARY KEY (user_id_1, user_id_2),
    CONSTRAINT fk_friend_user_1 FOREIGN KEY (user_id_1) REFERENCES users(user_id),
    CONSTRAINT fk_friend_user_2 FOREIGN KEY (user_id_2) REFERENCES users(user_id)
);