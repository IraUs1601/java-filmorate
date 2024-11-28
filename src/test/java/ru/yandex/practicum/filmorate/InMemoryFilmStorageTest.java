package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    public void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    public void shouldAddFilm() {
        Film film = new Film();
        film.setName("Test Film");

        Film addedFilm = filmStorage.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Test Film", addedFilm.getName());
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Initial Film");

        Film addedFilm = filmStorage.addFilm(film);

        addedFilm.setName("Updated Film");
        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    public void shouldGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        Film film2 = new Film();
        film2.setName("Film 2");

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        assertEquals(2, filmStorage.getAllFilms().size());
    }
}