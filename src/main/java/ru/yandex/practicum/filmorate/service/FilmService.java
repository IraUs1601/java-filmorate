package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.ValidationService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final ValidationService validationService;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public Film addFilm(Film film) {
        log.debug("Adding new film: {}", film);

        validateFilm(film);

        validationService.validate(film);
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Film added successfully: {}", addedFilm);
        return addedFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Updating film: {}", film);

        validateFilm(film);

        if (filmStorage.getFilmById(film.getId()) == null) {
            log.warn("Film with ID {} not found while updating film.", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with ID " + film.getId() + " not found.");
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Film updated successfully: {}", updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(int id) {
        log.debug("Fetching film by ID: {}", id);
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Film with ID {} not found while fetching film by ID.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with ID " + id + " not found.");
        }

        film.setGenres(
                film.getGenres().stream()
                        .map(genre -> {
                            genre.setName(translateGenreName(genre.getName()));
                            return genre;
                        })
                        .collect(Collectors.toList())
        );

        return film;
    }

    public void addLike(int filmId, int userId) {
        log.debug("Adding like to film ID: {} by user ID: {}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with ID " + filmId + " not found.");
        }

        if (userService.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }

        filmStorage.addLike(filmId, userId);
        log.info("Like added successfully to film ID: {}", filmId);
    }

    public void removeLike(int filmId, int userId) {
        log.debug("Removing like from film ID: {} by user ID: {}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film with ID " + filmId + " not found.");
        }

        if (userService.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }

        filmStorage.removeLike(filmId, userId);
        log.info("Like removed successfully from film ID: {}", filmId);
    }

    public List<Film> getAllFilms() {
        log.debug("Fetching all films");

        List<Film> allFilms = filmStorage.getAllFilms();
        if (allFilms == null || allFilms.isEmpty()) {
            log.warn("No films found in storage");
            return List.of();
        }

        log.debug("All films before returning: {}");
        return allFilms;
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Fetching popular films with count: {}", count);
        return filmStorage.getPopularFilms(count);
    }

    private String translateGenreName(String englishName) {
        return switch (englishName) {
            case "COMEDY" -> "Комедия";
            case "DRAMA" -> "Драма";
            case "ANIMATION" -> "Мультфильм";
            case "THRILLER" -> "Триллер";
            case "DOCUMENTARY" -> "Документальный";
            case "ACTION" -> "Боевик";
            default -> englishName;
        };
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film name cannot be empty");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film description cannot exceed 200 characters");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Release date cannot be before the first film was created (1895-12-28)");
        }

        if (film.getDuration() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film duration must be a positive number");
        }

        if (film.getMpaRating() == null || !validationService.isMpaRatingValid(film.getMpaRating().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid MPA rating ID: " + (film.getMpaRating() != null ? film.getMpaRating().getId() : "null"));
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!validationService.isGenreValid(genre.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid genre ID: " + genre.getId());
                }
            }
        }
    }
}