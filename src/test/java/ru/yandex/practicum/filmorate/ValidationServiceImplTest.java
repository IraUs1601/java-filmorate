package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationServiceImpl;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationServiceImplTest {

    private final ValidationServiceImpl validationService = new ValidationServiceImpl();

    @Test
    public void shouldThrowValidationExceptionForEmptyFilmName() {
        Film film = new Film();
        film.setDescription("Valid description.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validate(film));
        assertEquals("Film name cannot be empty", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionForInvalidUserEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validate(user));
        assertEquals("User email must be valid", exception.getMessage());
    }
}