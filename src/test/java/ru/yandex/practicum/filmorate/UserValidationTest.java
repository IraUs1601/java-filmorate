package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void initializeValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validUserShouldPassValidation() {
        User user = new User();
        user.setEmail("valid.user@example.com");
        user.setLogin("correctLogin");
        user.setName("Valid User");
        user.setBirthday(LocalDate.of(1985, 5, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "A valid user must pass validation.");
    }

    @Test
    public void shouldRejectInvalidEmailFormat() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("userLogin123");
        user.setBirthday(LocalDate.of(2000, 7, 15));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "An invalid email should trigger validation errors.");
    }

    @Test
    public void shouldNotAllowEmptyLogin() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setLogin(""); // Blank login
        user.setBirthday(LocalDate.of(1995, 10, 10));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "An empty login must fail validation.");
    }

    @Test
    public void futureBirthdaysShouldFailValidation() {
        User user = new User();
        user.setEmail("futureuser@example.com");
        user.setLogin("futureLogin");
        user.setBirthday(LocalDate.now().plusDays(10)); // Future date

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Birthdays in the future should not be valid.");
    }
}