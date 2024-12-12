package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.ValidationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final ValidationService validationService;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(FilmStorage filmStorage, UserService userService, ValidationService validationService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.validationService = validationService;
    }

    public Film addFilm(Film film) {
        log.debug("Adding new film: {}", film);
        validationService.validate(film);
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Film added successfully: {}", addedFilm);
        return addedFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Updating film: {}", film);
        validationService.validate(film);
        if (filmStorage.getFilmById(film.getId()) == null) {
            log.warn("Film with ID {} not found.", film.getId());
            throw new IllegalArgumentException("Film with ID " + film.getId() + " not found.");
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Film updated successfully: {}", updatedFilm);
        return updatedFilm;
    }

    public Film getFilmById(int id) {
        log.debug("Fetching film by ID: {}", id);
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Film with ID {} not found.", id);
            throw new IllegalArgumentException("Film with ID " + id + " not found.");
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        log.debug("Adding like to film ID: {} by user ID: {}", filmId, userId);
        Film film = getFilmById(filmId);

        if (userService.getUserById(userId) == null) {
            log.warn("User with ID {} not found. Cannot add like.", userId);
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }

        if (!film.getLikes().add(userId)) {
            log.warn("User ID: {} already liked film ID: {}", userId, filmId);
        } else {
            log.info("Like added successfully to film ID: {}", filmId);
        }
    }

    public void removeLike(int filmId, int userId) {
        log.debug("Removing like from film ID: {} by user ID: {}", filmId, userId);
        Film film = getFilmById(filmId);

        if (userService.getUserById(userId) == null) {
            log.warn("User with ID {} not found. Cannot remove like.", userId);
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }

        if (!film.getLikes().remove(userId)) {
            log.warn("User ID: {} had not liked film ID: {}", userId, filmId);
        } else {
            log.info("Like removed successfully from film ID: {}", filmId);
        }
    }

    public List<Film> getAllFilms() {
        log.debug("Fetching all films");
        List<Film> films = filmStorage.getAllFilms();
        if (films == null || films.isEmpty()) {
            log.warn("No films found in storage");
            return List.of();
        }
        return films;
    }

    public List<Film> getTopFilms(int count) {
        log.debug("Fetching top {} films by likes", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}