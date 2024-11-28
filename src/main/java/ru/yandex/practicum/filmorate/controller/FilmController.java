package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.ValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/films")
public class FilmController {

    private final ValidationService validationService;
    private final List<Film> films = new ArrayList<>();
    private int filmIdCounter = 1;

    @Autowired
    public FilmController(ValidationService validationService) {
        this.validationService = validationService;
        log.info("FilmController initialized with empty film list.");
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Fetching all films. Total films: {}", films.size());
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("Received request to create a new film: {}", film.getName());
        validationService.validate(film);

        film.setId(filmIdCounter++);
        films.add(film);
        log.info("Film created successfully with ID: {} and name: {}", film.getId(), film.getName());

        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) {
        log.info("Received request to update film with ID: {}", film.getId());
        validationService.validate(film);

        for (Film existingFilm : films) {
            if (existingFilm.getId() == film.getId()) {
                log.info("Film found. Updating film with ID: {}", film.getId());
                existingFilm.setName(film.getName());
                existingFilm.setDescription(film.getDescription());
                existingFilm.setReleaseDate(film.getReleaseDate());
                existingFilm.setDuration(film.getDuration());
                log.info("Film updated successfully with ID: {}", film.getId());
                return ResponseEntity.ok(existingFilm);
            }
        }

        log.warn("Film with ID {} not found. Update failed.", film.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Film with ID " + film.getId() + " not found."));
    }
}