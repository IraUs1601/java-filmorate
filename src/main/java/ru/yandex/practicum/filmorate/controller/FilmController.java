package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("Received request to create a new film: {}", film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Film created successfully: {}", createdFilm);
        return ResponseEntity.status(201).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Received request to update film: {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film updated successfully: {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        log.info("Received request to get film by ID: {}", id);
        Film film = filmService.getFilmById(id);
        log.info("Returning film: {}", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Received request to add like to film ID: {} by user ID: {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Like added successfully to film ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Received request to remove like from film ID: {} by user ID: {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Like removed successfully from film ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Fetching all films");
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            log.warn("No films found");
        } else {
            log.info("Total films retrieved: {}", films.size());
        }
        return ResponseEntity.ok(films);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Received request to get top {} popular films", count);
        List<Film> popularFilms = filmService.getTopFilms(count);
        log.info("Returning top {} films", popularFilms.size());
        return ResponseEntity.ok(popularFilms);
    }
}