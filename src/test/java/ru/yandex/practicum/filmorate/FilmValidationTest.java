package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.ValidationServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private ValidationServiceImpl validator;

    @BeforeEach
    public void setUp() {
        validator = new ValidationServiceImpl();
    }

    @Test
    public void shouldValidateCorrectFilm() {
        Film film = new Film();
        film.setName("Perfect Film");
        film.setDescription("Description within allowed length.");
        film.setReleaseDate(LocalDate.of(2000, 6, 15));
        film.setDuration(100);

        assertDoesNotThrow(() -> validator.validate(film),
                "A correctly configured film should pass validation.");
    }

    @Test
    public void shouldThrowExceptionForEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description.");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(film),
                "An empty name must fail validation.");

        assertEquals("Film name cannot be empty", exception.getMessage(),
                "Expected validation message for empty name.");
    }

    @Test
    public void shouldThrowExceptionForOverlyLongDescription() {
        Film film = new Film();
        film.setName("Another Film");
        film.setDescription("X".repeat(201));
        film.setReleaseDate(LocalDate.of(1995, 12, 25));
        film.setDuration(150);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(film),
                "A description exceeding 200 characters should fail validation.");

        assertEquals("Film description cannot exceed 200 characters", exception.getMessage(),
                "Expected validation message for overly long description.");
    }

    @Test
    public void shouldValidateReleaseDateOnCinemaStartDate() {
        Film film = new Film();
        film.setName("Historic Film");
        film.setDescription("A film released on cinema's start date.");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        assertDoesNotThrow(() -> validator.validate(film),
                "A release date of 28-12-1895 should pass validation.");
    }

    @Test
    public void shouldThrowExceptionForNegativeDuration() {
        Film film = new Film();
        film.setName("Short Film");
        film.setDescription("A short film with incorrect duration.");
        film.setReleaseDate(LocalDate.of(1985, 8, 1));
        film.setDuration(-50);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(film),
                "Negative duration should be flagged as invalid.");

        assertEquals("Film duration must be a positive number", exception.getMessage(),
                "Expected validation message for negative duration.");
    }

    @Test
    public void shouldThrowExceptionForInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Future Film");
        film.setDescription("A film from the future.");
        film.setReleaseDate(LocalDate.of(3000, 1, 1));
        film.setDuration(90);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(film),
                "A release date in the future must fail validation.");

        assertEquals("Film release date must be in the past or present", exception.getMessage(),
                "Expected validation message for release date in the future.");
    }
}