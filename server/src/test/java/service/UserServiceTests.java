package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//User service tests
public class UserServiceTests {
    private UserService userService;

    //fresh start before each test
    @BeforeEach
    public void setUp() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    //register works
    @Test
    public void registerSuccess() throws DataAccessException, AlreadyTakenException {
        AuthData result = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //cant register same username twice
    @Test
    public void registerDuplicate() throws DataAccessException, AlreadyTakenException {
        userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertThrows(AlreadyTakenException.class, () -> userService.register(new UserData("chloe", "1234", "chloe@email.com")));
    }

    //login works
    @Test
    public void loginSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        AuthData result = userService.login(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //wrong password cant login
    @Test
    public void loginWrongPassword() throws DataAccessException, AlreadyTakenException {
        userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertThrows(UnauthorizedException.class, () -> userService.login(new UserData("chloe", "wrongpassword", "")));
    }

    //logout works
    @Test
    public void logoutSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        AuthData auth = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertDoesNotThrow(() -> userService.logout(auth.authToken()));
    }

    //bad token cant logout
    @Test
    public void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("badtoken"));
    }
}