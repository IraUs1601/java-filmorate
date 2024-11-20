package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.ValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        log.info("UserController initialized. User list is empty.");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users. Total users: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        log.info("Received request to add a new user: {}", user.getLogin());
        validationService.validate(user);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("User name was empty. Set name to login: {}", user.getLogin());
        }

        user.setId(userIdCounter++);
        users.add(user);
        log.info("User added successfully with ID: {}, login: {}, display name: {}",
                user.getId(), user.getLogin(), user.getDisplayName());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> modifyUser(@RequestBody User user) {
        log.info("Received request to update user with ID: {}", user.getId());
        validationService.validate(user);

        for (User existingUser : users) {
            if (existingUser.getId() == user.getId()) {
                log.info("User found. Updating user with ID: {}", user.getId());
                existingUser.setEmail(user.getEmail());
                existingUser.setLogin(user.getLogin());
                existingUser.setName(user.getName() != null ? user.getName() : user.getLogin());
                existingUser.setBirthday(user.getBirthday());
                log.info("User updated successfully with ID: {}, display name: {}",
                        user.getId(), existingUser.getDisplayName());
                return ResponseEntity.ok(existingUser);
            }
        }

        log.warn("User with ID {} not found. Update failed.", user.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User with ID " + user.getId() + " not found."));
    }
}