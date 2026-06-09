package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//User service tests
class userManageTests {
    private userManage userManage;

    //fresh start before each test
    @BeforeEach
    void setUp() {
        DataAccess dataAccess = new MemoryDataAccess();
        userManage = new userManage(dataAccess);
    }

    //register works
    @Test
    void registerSuccess() throws DataException, BeenTakenException {
        AuthData result = userManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //cant register same username twice
    @Test
    void registerDuplicate() throws DataException, BeenTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        userManage.register(chloe);
        assertThrows(BeenTakenException.class, () -> userManage.register(chloe));
    }

    //login works
    @Test
    void loginSuccess() throws DataException, BeenTakenException, UnauthorizedException {
        userManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        AuthData result = userManage.login(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //wrong password cant login
    @Test
    void loginWrongPassword() throws DataException, BeenTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        var chloeWrongPassword = new UserData("chloe", "wrongpassword", "");
        userManage.register(chloe);
        assertThrows(UnauthorizedException.class, () -> userManage.login(chloeWrongPassword));
    }

    //logout works
    @Test
    void logoutSuccess() throws DataException, BeenTakenException, UnauthorizedException {
        AuthData auth = userManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertDoesNotThrow(() -> userManage.logout(auth.authToken()));
    }

    //bad token cant logout
    @Test
    void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> userManage.logout("badtoken"));
    }

    //missing fields = BadRequest
    @Test
    void registerMissingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequest.class, () -> userManage.register(noPassword));
    }

    //user that is not real cannot login
    @Test
    void loginNoSuchUser() {
        var ghost = new UserData("ghost", "1234", "");
        assertThrows(UnauthorizedException.class, () -> userManage.login(ghost));
    }

    //missing password
    @Test
    void missingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequest.class, () -> userManage.register(noPassword));
    }
}