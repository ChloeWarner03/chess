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
class UserServiceTests {
    private UserService userService;

    //fresh start before each test
    @BeforeEach
    void setUp() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    //register works
    @Test
    void registerSuccess() throws DataAccessException, AlreadyTakenException {
        AuthData result = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //cant register same username twice
    @Test
    void registerDuplicate() throws DataAccessException, AlreadyTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        userService.register(chloe);
        assertThrows(AlreadyTakenException.class, () -> userService.register(chloe));
    }

    //login works
    @Test
    void loginSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        AuthData result = userService.login(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //wrong password cant login
    @Test
    void loginWrongPassword() throws DataAccessException, AlreadyTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        var chloeWrongPassword = new UserData("chloe", "wrongpassword", "");
        userService.register(chloe);
        assertThrows(UnauthorizedException.class, () -> userService.login(chloeWrongPassword));
    }

    //logout works
    @Test
    void logoutSuccess() throws DataAccessException, AlreadyTakenException, UnauthorizedException {
        AuthData auth = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertDoesNotThrow(() -> userService.logout(auth.authToken()));
    }

    //bad token cant logout
    @Test
    void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("badtoken"));
    }

    //missing fields = BadRequestException
    @Test
    void registerMissingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequestException.class, () -> userService.register(noPassword));
    }

    //user that is not real cannot login
    @Test
    void loginNoSuchUser() {
        var ghost = new UserData("ghost", "1234", "");
        assertThrows(UnauthorizedException.class, () -> userService.login(ghost));
    }

    //missing password
    @Test
    void missingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequestException.class, () -> userService.register(noPassword));
    }
}