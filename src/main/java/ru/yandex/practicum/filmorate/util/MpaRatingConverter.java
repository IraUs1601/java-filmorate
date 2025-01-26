package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.MpaRating;

public class MpaRatingConverter {
    public static MpaRating fromId(int id) {
        return switch (id) {
            case 1 -> new MpaRating(1, "G");
            case 2 -> new MpaRating(2, "PG");
            case 3 -> new MpaRating(3, "PG-13");
            case 4 -> new MpaRating(4, "R");
            case 5 -> new MpaRating(5, "NC-17");
            default -> throw new IllegalArgumentException("Unknown MpaRating ID: " + id);
        };
    }

    public static int toId(MpaRating mpaRating) {
        return mpaRating.getId();
    }
}