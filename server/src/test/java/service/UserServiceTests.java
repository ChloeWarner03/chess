package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;

    //This runs before each test to give me a fresh start
    @BeforeEach
    public void setUp() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    //This tests that register works
    @Test
    public void registerSuccess() throws DataAccessException, AlreadyTakenException {
        UserData user = new UserData("chloe", "1234", "chloe@email.com");
        AuthData result = userService.register(user);
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //This tests that you cant register the same username twice
    @Test
    public void registerDuplicate() throws DataAccessException, AlreadyTakenException {
        UserData user = new UserData("chloe", "1234", "chloe@email.com");
        userService.register(user);
        assertThrows(AlreadyTakenException.class, () -> userService.register(user));
    }

    //This tests that login works
    @Test
    public void loginSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        UserData user = new UserData("chloe", "1234", "chloe@email.com");
        userService.register(user);
        AuthData result = userService.login(user);
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //This tests that wrong password fails
    @Test
    public void loginWrongPassword() throws DataAccessException, AlreadyTakenException {
        UserData user = new UserData("chloe", "1234", "chloe@email.com");
        userService.register(user);
        assertThrows(UnauthorizedException.class, () -> userService.login(new UserData("chloe", "wrongpassword", "")));
    }

    //This tests that logout works
    @Test
    public void logoutSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        UserData user = new UserData("chloe", "1234", "chloe@email.com");
        AuthData auth = userService.register(user);
        assertDoesNotThrow(() -> userService.logout(auth.authToken()));
    }

    //This tests that you cant logout with a bad token
    @Test
    public void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("badtoken"));
    }
}