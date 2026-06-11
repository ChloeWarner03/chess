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
class UserManageTests {
    private UserManage UserManage;

    //fresh start before each test
    @BeforeEach
    void setUp() {
        DataAccess dataAccess = new MemoryDataAccess();
        UserManage = new UserManage(dataAccess);
    }

    //register works
    @Test
    void registerSuccess() throws DataException, BeenTakenException {
        AuthData result = UserManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //cant register same username twice
    @Test
    void registerDuplicate() throws DataException, BeenTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        UserManage.register(chloe);
        assertThrows(BeenTakenException.class, () -> UserManage.register(chloe));
    }

    //login works
    @Test
    void loginSuccess() throws DataException, BeenTakenException, UnauthorizedException {
        UserManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        AuthData result = UserManage.login(new UserData("chloe", "1234", "chloe@email.com"));
        assertNotNull(result.authToken());
        assertEquals("chloe", result.username());
    }

    //wrong password cant login
    @Test
    void loginWrongPassword() throws DataException, BeenTakenException {
        var chloe = new UserData("chloe", "1234", "chloe@email.com");
        var chloeWrongPassword = new UserData("chloe", "wrongpassword", "");
        UserManage.register(chloe);
        assertThrows(UnauthorizedException.class, () -> UserManage.login(chloeWrongPassword));
    }

    //logout works
    @Test
    void logoutSuccess() throws DataException, BeenTakenException, UnauthorizedException {
        AuthData auth = UserManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        assertDoesNotThrow(() -> UserManage.logout(auth.authToken()));
    }

    //bad token cant logout
    @Test
    void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> UserManage.logout("Wrong Token"));
    }

    //missing fields = BadRequest
    @Test
    void registerMissingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequest.class, () -> UserManage.register(noPassword));
    }

    //user that is not real cannot login
    @Test
    void loginNoSuchUser() {
        var ghost = new UserData("ghost", "1234", "");
        assertThrows(UnauthorizedException.class, () -> UserManage.login(ghost));
    }

    //missing password
    @Test
    void missingFields() {
        var noPassword = new UserData("chloe", null, "chloe@email.com");
        assertThrows(BadRequest.class, () -> UserManage.register(noPassword));
    }
}