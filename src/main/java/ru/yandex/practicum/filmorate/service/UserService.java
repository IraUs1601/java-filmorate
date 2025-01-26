package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;

    public User addUser(User user) {
        validateUserData(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.debug("Updating user: {}", user);

        User existingUser = userStorage.getUserById(user.getId());
        if (existingUser == null) {
            log.warn("User with ID {} not found.", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + user.getId() + " not found");
        }

        User updatedUser = userStorage.updateUser(user);
        log.info("User updated successfully: {}", updatedUser);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        validateUserExistence(id);
        return userStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + friendId + " not found.");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.debug("Removing friend with ID {} from user with ID {}", friendId, userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("User with ID {} not found.", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.warn("Friend with ID {} not found.", friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend with ID " + friendId + " not found");
        }

        userStorage.removeFriend(userId, friendId);
        log.info("Friend with ID {} removed from user with ID {}", friendId, userId);
    }

    public List<User> getFriends(int userId) {
        log.debug("Fetching friends for user with ID {}", userId);

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("User with ID {} not found.", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found");
        }

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        validateUserExistence(userId);
        validateUserExistence(otherUserId);
        List<User> userFriends = getFriends(userId);
        List<User> otherUserFriends = getFriends(otherUserId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private void validateUserExistence(int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }
    }

    private void validateUserData(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login cannot be empty.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday cannot be in the future.");
        }
    }
}