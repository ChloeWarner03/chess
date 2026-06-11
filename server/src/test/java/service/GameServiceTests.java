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
class GamePlayManageTests {
    private GamePlayManage GamePlayManage;
    private UserManage UserManage;
    private String authToken;

    //fresh start before each test
    @BeforeEach
    void setUp() throws DataException, BeenTakenException {
        DataAccess dataAccess = new MemoryDataAccess();
        GamePlayManage = new GamePlayManage(dataAccess);
        UserManage = new UserManage(dataAccess);
        //register chloe to get a valid token
        AuthData auth = UserManage.register(new UserData("chloe", "1234", "chloe@email.com"));
        authToken = auth.authToken();
    }

    //create game works
    @Test
    void createSuccess() throws DataException, UnauthorizedException, BadRequest {
        int gameID = GamePlayManage.createGame(authToken, "My Chess Game");
        assertTrue(gameID > 0);
    }
    //Cant make a game with no name
    @Test
    void createGameNoName() {
        assertThrows(BadRequest.class, () -> GamePlayManage.createGame(authToken, null));
    }

    //bad token cant create game
    @Test
    void unauthorizedCreate() {
        assertThrows(UnauthorizedException.class, () -> GamePlayManage.createGame("Token Does Not Work", "myGame"));
    }

    //list games works
    @Test
    void listSuccess() throws DataException, UnauthorizedException, BadRequest {
        GamePlayManage.createGame(authToken, "My Chess Game");
        List<GameData> games = GamePlayManage.listGames(authToken);
        assertEquals(1, games.size());
    }

    //bad token cant list games
    @Test
    void unauthorizedList() {
        assertThrows(UnauthorizedException.class, () -> GamePlayManage.listGames("badtoken"));
    }

    //join game works
    @Test
    void joinSuccess() throws DataException, UnauthorizedException, BadRequest, BeenTakenException {
        int gameID = GamePlayManage.createGame(authToken, "My Chess Game");
        assertDoesNotThrow(() -> GamePlayManage.joinGame(authToken, "WHITE", gameID));
    }

    //cant join taken color
    @Test
    void colorTaken() throws DataException, UnauthorizedException, BadRequest, BeenTakenException {
        int gameID = GamePlayManage.createGame(authToken, "My Chess Game");
        GamePlayManage.joinGame(authToken, "WHITE", gameID);
        assertThrows(BeenTakenException.class, () -> GamePlayManage.joinGame(authToken, "WHITE", gameID));
    }

    //clear works
    @Test //This one still says to remove the public modifier
    void clearSuccess() {
        assertDoesNotThrow(() -> GamePlayManage.clear());
    }

    // bad token cant join game
    @Test
    void joinUnauthorized() throws DataException,
            UnauthorizedException, BadRequest {
        int gameID = GamePlayManage.createGame(authToken, "My Chess Game");
        assertThrows(UnauthorizedException.class,
                () -> GamePlayManage.joinGame("Token Does Not Work", "BLACK", gameID));
    }
    // invalid color
    @Test
    void joinInvalidColor() throws DataException,
            UnauthorizedException, BadRequest {
        int gameID = GamePlayManage.createGame(authToken, "My Chess Game");
        assertThrows(BadRequest.class,
                () -> GamePlayManage.joinGame(authToken, "PURPLE", gameID));
    }
    // Cannot join a game that does not exist
    @Test
    void joinBadGameID() {
        assertThrows(BadRequest.class,
                () -> GamePlayManage.joinGame(authToken, "BLACK", 39393));
    }

    //no games there is no list
    @Test
    void listEmpty() throws DataException, UnauthorizedException {
        List<GameData> games = GamePlayManage.listGames(authToken);
        assertTrue(games.isEmpty());
    }
}
