package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

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
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(films);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            validationService.validate(film);
            film.setId(filmIdCounter++);
            films.add(film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            validationService.validate(film);
            for (Film existingFilm : films) {
                if (existingFilm.getId() == film.getId()) {
                    existingFilm.setName(film.getName());
                    existingFilm.setDescription(film.getDescription());
                    existingFilm.setReleaseDate(film.getReleaseDate());
                    existingFilm.setDuration(film.getDuration());
                    return ResponseEntity.ok(existingFilm);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}