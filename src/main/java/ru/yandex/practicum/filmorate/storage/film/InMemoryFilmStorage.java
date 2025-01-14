package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmIdCounter = 1;

    @Override
    public Film addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.setId(filmIdCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Film with ID " + film.getId() + " not found.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Film with ID " + filmId + " not found.");
        }
        if (!film.getLikes().add(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " already liked the film.");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Film with ID " + filmId + " not found.");
        }
        if (!film.getLikes().remove(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " has not liked the film.");
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likeComparison = Integer.compare(f2.getLikes().size(), f1.getLikes().size());
                    if (likeComparison != 0) {
                        return likeComparison;
                    }
                    return Integer.compare(f1.getId(), f2.getId());
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}