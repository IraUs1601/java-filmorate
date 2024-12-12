# java-filmorate
Template repository for the Filmorate project.

## Схема базы данных

Ниже представлена схема базы данных для приложения. Она включает следующие таблицы:
- **Пользователи (users)** – информация о пользователях (имя, почта, логин, день рождения).
- **Фильмы (films)** – данные о фильмах (название, описание, дата выхода, продолжительность).
- **Жанры (genres)** – жанры фильмов.
- **Рейтинги (mpa_ratings)** – возрастные рейтинги фильмов.
- **Связи между пользователями (friendships)** – дружеские отношения между пользователями.
- **Лайки (likes)** – информация о том, какие фильмы понравились пользователям.

![Диаграмма базы данных](https://github.com/IraUs1601/java-filmorate/blob/main/images/filmorate.png)

### Примеры запросов

1. **Получение списка всех фильмов с их жанрами и возрастным рейтингом**:

    ```sql
SELECT f.title, f.description, g.name AS genre, m.rating AS mpa_rating
FROM films f
JOIN genres g ON f.genre_id = g.genre_id
JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id;
```

2. **Получение всех фильмов, которые понравились конкретному пользователю**:

    ```sql
    SELECT f.title, f.description
FROM likes l
JOIN films f ON l.film_id = f.film_id
WHERE l.user_id = 1 AND l.like_status = true;
```

3. **Получение списка друзей пользователя с именами**:

    ```sql
    SELECT u.name AS friend_name, fr.status
FROM friendships fr
JOIN users u ON (fr.user_id_1 = u.user_id OR fr.user_id_2 = u.user_id)
WHERE (fr.user_id_1 = 1 OR fr.user_id_2 = 1) AND u.user_id != 1;
```

4. **Получение списка всех пользователей, которые лайкнули определенный фильм**:

    ```sql
    SELECT u.name, u.email
FROM likes l
JOIN users u ON l.user_id = u.user_id
WHERE l.film_id = 1 AND l.like_status = true;
```

5. **Получение количества фильмов в каждом жанре**:

    ```sql
    SELECT g.name AS genre, COUNT(f.film_id) AS film_count
FROM genres g
LEFT JOIN films f ON g.genre_id = f.genre_id
GROUP BY g.name;
```