package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.ValidationServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private ValidationServiceImpl validationService;
    private InMemoryUserStorage userStorage;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        filmStorage = new InMemoryFilmStorage();
        validationService = new ValidationServiceImpl();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userService, validationService);
    }

    @Test
    public void shouldAddFilm() {
        Film film = new Film();
        film.setName("New Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmService.addFilm(film);

        assertNotNull(addedFilm.getId(), "Film ID should not be null after addition.");
        assertEquals("New Film", addedFilm.getName(), "Film name should be as set.");
        assertEquals(1, filmStorage.getAllFilms().size(), "Storage should contain 1 film after addition.");
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Initial Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film addedFilm = filmService.addFilm(film);
        addedFilm.setName("Updated Film");

        Film updatedFilm = filmService.updateFilm(addedFilm);

        assertEquals("Updated Film", updatedFilm.getName(), "Film name should be updated.");
        assertEquals(1, filmStorage.getAllFilms().size(), "Storage should still contain 1 film.");
    }
}