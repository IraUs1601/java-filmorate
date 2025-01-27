package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface ValidationService {
    void validate(Film film) throws ValidationException;

    void validate(User user) throws ValidationException;

    boolean isMpaRatingValid(int mpaRatingId);

    boolean isGenreValid(int genreId);
}