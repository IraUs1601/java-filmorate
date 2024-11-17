package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotEmpty(message = "The name field must not be empty.")
    private String name;

    @NotEmpty(message = "The description field must not be empty.")
    @Size(max = 200, message = "The description must be 200 characters or fewer.")
    private String description;

    @NotNull(message = "Release date cannot be null")
    @PastOrPresent(message = "Release date must be in the past or present")
    private LocalDate releaseDate;

    @Positive(message = "The duration must be greater than zero.")
    private int duration;
}