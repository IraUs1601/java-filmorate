package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SuppressWarnings("unused")
@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {
    private static final LocalDate CINEMA_START_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void validate(Film film) {
        log.info("Validating film: {}", film.getName());

        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("Validation failed: Film name is empty");
            throw new ValidationException("Film name cannot be empty");
        }
        if (film.getDescription() == null || film.getDescription().isEmpty()) {
            log.warn("Validation failed: Film description is empty");
            throw new ValidationException("Film description cannot be empty");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Validation failed: Film description exceeds 200 characters");
            throw new ValidationException("Film description cannot exceed 200 characters");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_START_DATE)) {
            log.warn("Validation failed: Film release date is invalid");
            throw new ValidationException("Film release date cannot be before 28-12-1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Validation failed: Film duration is non-positive");
            throw new ValidationException("Film duration must be a positive number");
        }
        log.info("Film validated successfully: {}", film.getName());
    }

    @Override
    public void validate(User user) {
        log.info("Validating user: {}", user.getLogin());

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("Validation failed: User email is empty");
            throw new ValidationException("User email cannot be empty");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Validation failed: User email is invalid");
            throw new ValidationException("User email must be valid");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            log.warn("Validation failed: User login is empty");
            throw new ValidationException("User login cannot be empty");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Validation failed: User birthday is in the future");
            throw new ValidationException("User birthday cannot be in the future");
        }
        log.info("User validated successfully: {}", user.getLogin());
    }
}