package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Adding new user: {}", user);
        validateUser(user);
        User addedUser = userStorage.addUser(user);
        log.info("User added successfully: {}", addedUser);
        return addedUser;
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        validateUser(user);
        User updatedUser = userStorage.updateUser(user);
        log.info("User updated successfully: {}", updatedUser);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userStorage.getAllUsers();
        log.debug("Total users retrieved: {}", users.size());
        return users;
    }

    public User getUserById(int id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("User with ID {} not found.", id);
            throw new IllegalArgumentException("User with ID " + id + " not found.");
        }
        log.debug("User found: {}", user);
        return user;
    }

    public void addFriend(int userId, int friendId) {
        log.info("Adding friend with ID: {} to user ID: {}", friendId, userId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (!user.getFriends().add(friendId)) {
            log.warn("Friend ID: {} is already in user ID: {}'s friend list.", friendId, userId);
        } else {
            friend.getFriends().add(userId);
            log.info("Friend added successfully: User ID: {} <--> Friend ID: {}", userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Removing friend with ID: {} from user ID: {}", friendId, userId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (!user.getFriends().remove(friendId)) {
            log.warn("Friend ID: {} not found in user ID: {}'s friend list.", friendId, userId);
        } else {
            friend.getFriends().remove(userId);
            log.info("Friend removed successfully: User ID: {} <--> Friend ID: {}", userId, friendId);
        }
    }

    public List<User> getFriends(int userId) {
        log.debug("Fetching friends for user ID: {}", userId);
        User user = getUserById(userId);
        List<User> friends = user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.debug("Total friends retrieved for user ID {}: {}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Fetching common friends between user ID: {} and user ID: {}", userId, otherUserId);
        List<User> userFriends = getFriends(userId);
        List<User> otherUserFriends = getFriends(otherUserId);
        List<User> commonFriends = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
        log.info("Common friends retrieved: {}", commonFriends);
        return commonFriends;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Validation failed: Invalid email");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Invalid email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Validation failed: Login cannot be empty");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Login cannot be empty");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Setting default name to login for user: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(java.time.LocalDate.now())) {
            log.warn("Validation failed: Birthday cannot be in the future");
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("Birthday cannot be in the future");
        }
    }
}