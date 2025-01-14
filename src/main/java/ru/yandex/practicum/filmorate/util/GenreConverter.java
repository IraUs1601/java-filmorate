package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.Genre;

public class GenreConverter {
    public static Genre fromId(int id) {
        return switch (id) {
            case 1 -> new Genre(1, "COMEDY");
            case 2 -> new Genre(2, "DRAMA");
            case 3 -> new Genre(3, "ANIMATION");
            case 4 -> new Genre(4, "THRILLER");
            case 5 -> new Genre(5, "DOCUMENTARY");
            case 6 -> new Genre(6, "ACTION");
            default -> throw new IllegalArgumentException("Unknown Genre ID: " + id);
        };
    }

    public static int toId(Genre genre) {
        return genre.getId();
    }
}