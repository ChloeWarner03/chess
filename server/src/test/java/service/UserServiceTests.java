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
}