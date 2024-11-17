package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NotEmpty(message = "The email field must not be empty.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotEmpty(message = "The login field must not be empty.")
    private String login;

    @Size(max = 100, message = "The name must not exceed 100 characters.")
    private String name;

    @PastOrPresent(message = "The birthday cannot be set to a future date.")
    private LocalDate birthday;

    public String getDisplayName() {
        return (name == null || name.trim().isEmpty()) ? login : name;
    }
}