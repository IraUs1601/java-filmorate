package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.time.LocalDate;

@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {
    private static final LocalDate CINEMA_START_DATE = LocalDate.of(1895, 12, 28);
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;

    public ValidationServiceImpl(MpaRatingStorage mpaRatingStorage, GenreStorage genreStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public void validate(Film film) {
        log.info("Validating film: {}", film.getName());
    }

    @Override
    public void validate(User user) {
        log.info("Validating user: {}", user.getLogin());
    }

    @Override
    public boolean isMpaRatingValid(int mpaRatingId) {
        return mpaRatingId > 0 && mpaRatingStorage.existsById(mpaRatingId);
    }

    @Override
    public boolean isGenreValid(int genreId) {
        return genreId > 0 && genreStorage.existsById(genreId);
    }
}