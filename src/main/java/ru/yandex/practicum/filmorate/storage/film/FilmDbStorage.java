package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.util.GenreConverter;
import ru.yandex.practicum.filmorate.util.MpaRatingConverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpaRating() == null || film.getMpaRating().getId() == 0) {
            throw new IllegalArgumentException("Invalid MpaRating");
        }

        String sql = "INSERT INTO films (title, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaRating().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        saveGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                MpaRatingConverter.toId(film.getMpaRating()),
                film.getId());

        String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());
        saveGenres(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);

        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Integer> genreIds = jdbcTemplate.queryForList(genresSql, Integer.class, id);
        film.setGenres(genreIds.stream().map(GenreConverter::fromId).collect(Collectors.toList()));

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        for (Film film : films) {
            String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
            List<Integer> genreIds = jdbcTemplate.queryForList(genresSql, Integer.class, film.getId());
            film.setGenres(genreIds.stream().map(GenreConverter::fromId).collect(Collectors.toList()));
        }

        return films;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (user_id, film_id, like_status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, filmId, true);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.*, COUNT(l.user_id) AS like_count
            FROM films f
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id
            ORDER BY like_count DESC, f.title
            LIMIT ?
            """;
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private void saveGenres(int filmId, List<Genre> genres) {
        String checkSql = "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?";
        String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId, GenreConverter.toId(genre));
            if (count == 0) {
                jdbcTemplate.update(insertSql, filmId, GenreConverter.toId(genre));
            }
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getInt("film_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                MpaRatingConverter.fromId(rs.getInt("mpa_rating_id")),
                new ArrayList<>(),
                new HashSet<>()
        );

        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Integer> genreIds = jdbcTemplate.queryForList(genresSql, Integer.class, film.getId());
        film.setGenres(genreIds.stream().map(GenreConverter::fromId).collect(Collectors.toList()));

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.queryForList(likesSql, Integer.class, film.getId()));
        film.setLikes(likes);

        return film;
    }
}