package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@SuppressWarnings("unused")
public class UserController {

    private final List<User> users = new ArrayList<>();
    private final ValidationService validationService;
    private int userIdCounter = 1;

    public UserController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users.");
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            validationService.validate(user);
            user.setId(userIdCounter++);
            users.add(user);
            log.info("User created successfully with login: {}", user.getLogin());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException ex) {
            log.error("Failed to create user: {}", ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<User> modifyUser(@RequestBody User user) {
        try {
            validationService.validate(user);
            for (User existingUser : users) {
                if (existingUser.getId() == user.getId()) {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setLogin(user.getLogin());
                    existingUser.setName(user.getName() != null ? user.getName() : user.getLogin());
                    existingUser.setBirthday(user.getBirthday());
                    log.info("User updated successfully with ID: {}", user.getId());
                    return ResponseEntity.ok(existingUser);
                }
            }
            log.warn("User with ID {} not found for update.", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException ex) {
            log.error("Failed to update user: {}", ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}