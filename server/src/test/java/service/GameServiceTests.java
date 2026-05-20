package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

//Game service tests
public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private String authToken;

    //fresh start before each test
    @BeforeEach
    public void setUp() throws DataAccessException, AlreadyTakenException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        //register chloe to get a valid token
        AuthData auth = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        authToken = auth.authToken();
    }

    //create game works
    @Test
    public void createSuccess() throws DataAccessException, UnauthorizedException, BadRequestException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertTrue(gameID > 0);
    }

    //bad token cant create game
    @Test
    public void unauthorizedCreate() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("badtoken", "myGame"));
    }

    //list games works
    @Test
    public void listSuccess() throws DataAccessException, UnauthorizedException, BadRequestException {
        gameService.createGame(authToken, "myGame");
        List<GameData> games = gameService.listGames(authToken);
        assertEquals(1, games.size());
    }

    //bad token cant list games
    @Test
    public void unauthorizedList() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("badtoken"));
    }

    //join game works
    @Test
    public void joinSuccess() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertDoesNotThrow(() -> gameService.joinGame(authToken, "WHITE", gameID));
    }

    //cant join taken color
    @Test
    public void colorTaken() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        int gameID = gameService.createGame(authToken, "myGame");
        gameService.joinGame(authToken, "WHITE", gameID);
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(authToken, "WHITE", gameID));
    }

    //clear works
    @Test
    public void clearSuccess() throws DataAccessException {
        assertDoesNotThrow(() -> gameService.clear());
    }

    // bad token cant join game
    @Test
    public void joinUnauthorized() throws DataAccessException,
            UnauthorizedException, BadRequestException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame("badtoken", "WHITE", gameID));
    }
    // invalid color throws BadRequestException
    @Test
    public void joinInvalidColor() throws DataAccessException,
            UnauthorizedException, BadRequestException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(authToken, "PURPLE", gameID));
    }
    // Cannot join a game that does not exist
    @Test
    public void joinBadGameID() {
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(authToken, "WHITE", 99999));
    }

    //Base case when there are no games there is no list
    @Test
    public void listEmpty() throws DataAccessException, UnauthorizedException {
        List<GameData> games = gameService.listGames(authToken);
        assertTrue(games.isEmpty());
    }
}
