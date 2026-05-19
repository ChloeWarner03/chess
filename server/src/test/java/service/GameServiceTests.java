package service;

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

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private String authToken;

    //This runs before each test to give me a fresh start
    @BeforeEach
    public void setUp() throws DataAccessException, AlreadyTakenException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
        //Register a user to get a valid auth token
        AuthData auth = userService.register(new UserData("chloe", "1234", "chloe@email.com"));
        authToken = auth.authToken();
    }

    //This tests that creating a game works
    @Test
    public void createGameSuccess() throws DataAccessException, UnauthorizedException, BadRequestException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertTrue(gameID > 0);
    }

    //This tests that creating a game with bad token fails
    @Test
    public void createGameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("badtoken", "myGame"));
    }

    //This tests that listing games works
    @Test
    public void listGamesSuccess() throws DataAccessException, UnauthorizedException, BadRequestException {
        gameService.createGame(authToken, "myGame");
        List<GameData> games = gameService.listGames(authToken);
        assertEquals(1, games.size());
    }

    //This tests that listing games with bad token fails
    @Test
    public void listGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("badtoken"));
    }

    //This tests that joining a game works
    @Test
    public void joinGameSuccess() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        int gameID = gameService.createGame(authToken, "myGame");
        assertDoesNotThrow(() -> gameService.joinGame(authToken, "WHITE", gameID));
    }

    //This tests that joining a taken color fails
    @Test
    public void joinGameColorTaken() throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        int gameID = gameService.createGame(authToken, "myGame");
        gameService.joinGame(authToken, "WHITE", gameID);
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(authToken, "WHITE", gameID));
    }

    //This tests that clear works
    @Test
    public void clearSuccess() throws DataAccessException {
        assertDoesNotThrow(() -> gameService.clear());
    }
}