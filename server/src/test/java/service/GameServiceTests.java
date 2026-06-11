package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

//Game service tests
class gameplayManageTests {
    private gameplayManage gameplayManage;
    private userManage userManage;
    private String authToken;

    //fresh start before each test
    @BeforeEach
    void setUp() throws DataException, BeenTakenException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameplayManage = new gameplayManage(dataAccess);
        userManage = new userManage(dataAccess);
        //register chloe to get a valid token
        AuthData auth = userManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        authToken = auth.authToken();
    }

    //create game works
    @Test
    void createSuccess() throws DataException, UnauthorizedException, BadRequest {
        int gameID = gameplayManage.createGame(authToken, "My Chess Game");
        assertTrue(gameID > 0);
    }
    //Cant make a game with no name
    @Test
    void createGameNoName() {
        assertThrows(BadRequest.class, () -> gameplayManage.createGame(authToken, null));
    }

    //bad token cant create game
    @Test
    void unauthorizedCreate() {
        assertThrows(UnauthorizedException.class, () -> gameplayManage.createGame("Token Does Not Work", "myGame"));
    }

    //list games works
    @Test
    void listSuccess() throws DataException, UnauthorizedException, BadRequest {
        gameplayManage.createGame(authToken, "My Chess Game");
        List<GameData> games = gameplayManage.listGames(authToken);
        assertEquals(1, games.size());
    }

    //bad token cant list games
    @Test
    void unauthorizedList() {
        assertThrows(UnauthorizedException.class, () -> gameplayManage.listGames("badtoken"));
    }

    //join game works
    @Test
    void joinSuccess() throws DataException, UnauthorizedException, BadRequest, BeenTakenException {
        int gameID = gameplayManage.createGame(authToken, "My Chess Game");
        assertDoesNotThrow(() -> gameplayManage.joinGame(authToken, "WHITE", gameID));
    }

    //cant join taken color
    @Test
    void colorTaken() throws DataException, UnauthorizedException, BadRequest, BeenTakenException {
        int gameID = gameplayManage.createGame(authToken, "My Chess Game");
        gameplayManage.joinGame(authToken, "WHITE", gameID);
        assertThrows(BeenTakenException.class, () -> gameplayManage.joinGame(authToken, "WHITE", gameID));
    }

    //clear works
    @Test //This one still says to remove the public modifier
    void clearSuccess() {
        assertDoesNotThrow(() -> gameplayManage.clear());
    }

    // bad token cant join game
    @Test
    void joinUnauthorized() throws DataException,
            UnauthorizedException, BadRequest {
        int gameID = gameplayManage.createGame(authToken, "My Chess Game");
        assertThrows(UnauthorizedException.class,
                () -> gameplayManage.joinGame("Token Does Not Work", "BLACK", gameID));
    }
    // invalid color
    @Test
    void joinInvalidColor() throws DataException,
            UnauthorizedException, BadRequest {
        int gameID = gameplayManage.createGame(authToken, "My Chess Game");
        assertThrows(BadRequest.class,
                () -> gameplayManage.joinGame(authToken, "PURPLE", gameID));
    }
    // Cannot join a game that does not exist
    @Test
    void joinBadGameID() {
        assertThrows(BadRequest.class,
                () -> gameplayManage.joinGame(authToken, "BLACK", 39393));
    }

    //no games there is no list
    @Test
    void listEmpty() throws DataException, UnauthorizedException {
        List<GameData> games = gameplayManage.listGames(authToken);
        assertTrue(games.isEmpty());
    }
}
