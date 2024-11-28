package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.ValidationServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private ValidationServiceImpl validationService;

    @BeforeEach
    public void setUp() {
        validationService = new ValidationServiceImpl();
    }

    @Test
    public void validUserShouldPassValidation() {
        User user = new User();
        user.setEmail("valid.user@example.com");
        user.setLogin("correctLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1985, 5, 20));

        assertDoesNotThrow(() -> validationService.validate(user),
                "A valid user must pass validation.");
    }

    @Test
    public void shouldRejectInvalidEmailFormat() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("userLogin123");
        user.setBirthday(LocalDate.of(2000, 7, 15));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationService.validate(user),
                "An invalid email should trigger validation errors.");

        assertEquals("User email must be valid", exception.getMessage(),
                "Expected validation message for invalid email format.");
    }

    @Test
    public void shouldNotAllowEmptyLogin() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin(""); // Пустой логин
        user.setBirthday(LocalDate.of(1995, 10, 10));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationService.validate(user),
                "An empty login must fail validation.");

        assertEquals("User login cannot be empty", exception.getMessage(),
                "Expected validation message for empty login.");
    }

    @Test
    public void futureBirthdaysShouldFailValidation() {
        User user = new User();
        user.setEmail("futureuser@example.com");
        user.setLogin("futureLogin");
        user.setBirthday(LocalDate.now().plusDays(10)); // Дата в будущем

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validationService.validate(user),
                "Birthdays in the future should not be valid.");

        assertEquals("User birthday cannot be in the future", exception.getMessage(),
                "Expected validation message for future birthday.");
    }

    @Test
    public void shouldAcceptNullNameAndDefaultToLogin() {
        User user = new User();
        user.setEmail("defaultname@example.com");
        user.setLogin("defaultLogin");
        user.setBirthday(LocalDate.of(1990, 3, 14));
        user.setName(null); // Name is null

        assertDoesNotThrow(() -> validationService.validate(user),
                "A null name should pass validation and default to login.");

        assertEquals("defaultLogin", user.getName(),
                "If name is null, it should default to login.");
    }
}