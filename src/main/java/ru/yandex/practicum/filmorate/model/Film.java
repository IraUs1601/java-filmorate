package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotEmpty(message = "The name field must not be empty.")
    private String name;

    @NotEmpty(message = "The description field must not be empty.")
    @Size(max = 200, message = "The description must be 200 characters or fewer.")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "The duration must be greater than zero.")
    private int duration;
}