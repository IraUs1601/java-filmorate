package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void shouldAddUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");

        User addedUser = userService.addUser(user);

        assertNotNull(addedUser.getId(), "User ID should not be null after addition.");
        assertEquals("user@example.com", addedUser.getEmail(), "Email should match the provided value.");
        assertEquals(1, userStorage.getAllUsers().size(), "Storage should contain 1 user after addition.");
    }

    @Test
    public void shouldGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1Login");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2Login");

        userService.addUser(user1);
        userService.addUser(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size(), "There should be 2 users in the storage.");
        assertTrue(users.contains(user1), "The storage should contain user1.");
        assertTrue(users.contains(user2), "The storage should contain user2.");
    }
}