package dataaccess;

//my imports
import model.UserData;
import org.junit.jupiter.api.*;
import model.GameData;
import chess.ChessGame;

//can this be used
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlTestsPersonal {
    private static SqlAccess data;

    //setting it al up for the tests
    @BeforeAll
    static void setitallup() throws DataAccessException {
        data = new SqlAccess();
    }

    //This one clears the Database
    @BeforeEach
    void clearDatabase() throws DataAccessException {
        // This will clear before the tests
        data.clear();
    }

    //createUser: these will be the tests for creatUser
    //create a user
    @Test
    void createanewUser() throws DataAccessException {
        // add a user
        data.createUser(new UserData("chloe", "password123", "chloe@email.com"));
        // make sure they are there
        assertNotNull(data.getUser("chloe"));
    }

    //This test will make it so then there is no duplicates
    @Test
    void sameUser() throws DataAccessException {
        // add a user
        data.createUser(new UserData("chloe", "pass", "chloe@email.com"));
        // adding same user should fail
        assertThrows(DataAccessException.class, () ->
                data.createUser(new UserData("chloe", "pass", "chloe@email.com")));
    }
    //grab the user
    void grabUser() throws DataAccessException {
        // add a user first
        data.createUser(new UserData("chloe", "pass", "chloe@email.com"));
        // them find and grab them
        assertNotNull(data.getUser("chloe"));
    }
    //These are going to be for the Game
    //Create a new Game
    @Test
    void createNewGame() throws DataAccessException {
        // make a new game
        var gameID = data.createGame(new GameData(0, null, null, "testGame", new ChessGame()));
        // should have a real id
        assertTrue(gameID > 0);
    }

    //The Game needs to have a name
    @Test
    void gameNeedsName() throws DataAccessException {
        // game with no name should fail
        assertThrows(DataAccessException.class, () ->
                data.createGame(new GameData(0, null, null, null, new ChessGame())));
    }

    //grab the game
    @Test
    void getGameWorks() throws DataAccessException {
        // make a game then get it back
        var gameID = data.createGame(new GameData(0, null, null, "testGame", new ChessGame()));
        assertNotNull(data.getGame(gameID));
    }
    //Cannot find a game
    void getGameNotFound() throws DataAccessException {
        // game that doesnt exist should return null
        assertNull(data.getGame(123456789));
    }
}