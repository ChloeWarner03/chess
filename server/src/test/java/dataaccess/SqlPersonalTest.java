package dataaccess;
//trying again for errors
//my imports
import model.UserData;
import org.junit.jupiter.api.*;
import model.GameData;
import chess.ChessGame;
import model.AuthData;
//submit for code quality
//can this be used
import static org.junit.jupiter.api.Assertions.*;


class SqlPersonalTest {
    private static SqlAccess data;

    //setting it al up for the tests
    @BeforeAll
    static void setitallup() throws DataException {
        data = new SqlAccess();
    }

    //This one clears the Database
    @BeforeEach
    void clearDatabase() throws DataException {
        // This will clear before the tests
        data.clear();
    }

    //makeChessUser: these will be the tests for creatUser
    //create a user
    @Test
    void createanewUser() throws DataException {
        // add a user
        data.makeChessUser(new UserData("chloe", "password123", "chloe@email.com"));
        // make sure they are there
        assertNotNull(data.getUser("chloe"));
    }

    @Test
    void getUserNeg() throws DataException {
        assertNull(data.getUser("NotRealUser"));
    }

    //This test will make it so then there is no duplicates
    @Test
    void sameUser() throws DataException {
        // add a user
        data.makeChessUser(new UserData("chloe", "pass", "chloe@email.com"));
        // adding same user should fail
        assertThrows(DataException.class, () ->
                data.makeChessUser(new UserData("chloe", "pass", "chloe@email.com")));
    }
    //grab the user
    @Test
    void grabUser() throws DataException {
        // add a user first
        data.makeChessUser(new UserData("chloe", "pass", "chloe@email.com"));
        // them find and grab them
        assertNotNull(data.getUser("chloe"));
    }
    //These are going to be for the Game
    //Create a new Game
    @Test
    void createNewGame() throws DataException {
        // make a new game
        var gameID = data.createGame(new GameData(0, null, null, "testGame", new ChessGame()));
        // should have a real id
        assertTrue(gameID > 0);
    }

    //The Game needs to have a name
    @Test
    void gameNeedsName() {
        // game with no name should fail
        assertThrows(DataException.class, () ->
                data.createGame(new GameData(0, null, null, null, new ChessGame())));
    }

    //grab the game
    @Test
    void getGameWorks() throws DataException {
        // make a game then get it back
        var gameID = data.createGame(new GameData(0, null, null, "testGame", new ChessGame()));
        assertNotNull(data.getGame(gameID));
    }
    //Cannot find a game
    @Test
    void getGameNotFound() throws DataException {
        // game that doesnt exist should return null
        assertNull(data.getGame(123456789));
    }
    //List Games: need Pos and Neg
    //empty game list
    @Test
    void gameEmpty() throws DataException {
        // no games should return empty list
        assertEquals(0, data.listGames().size());
    }
    @Test
    void listGamesWorks() throws DataException {
        // add games
        data.createGame(new GameData(0, null, null, "GAMENUMBER1", new ChessGame()));
        data.createGame(new GameData(0, null, null, "GAMENUMBER2", new ChessGame()));
        // needs 2 games
        assertEquals(2, data.listGames().size());
    }
    //updateGame: need Pos and Neg
    //UPdate a real game
    @Test
    void updateRealGame() throws DataException {
        //make a real gamemake a game
        var gameID = data.createGame(new GameData(0, null, null, "RealGame", new ChessGame()));
        // make player
        data.updateGame(new GameData(gameID, "chloe", null, "RealGame", new ChessGame()));
        //update worked
        assertEquals("chloe", data.getGame(gameID).whiteUsername());
    }
    //Cant update a game that does not exist
    @Test
    void updateFakeGame() throws DataException {
        // game does not exist= fail
        data.updateGame(new GameData(36921, "chloe", null, "FakeGame", new ChessGame()));
        // game still should not exist
        assertNull(data.getGame(36921));
    }
    //createAuth need Pos and Neg
    @Test
    void createAuthentification() throws DataException {
        // make an auth token
        data.makeAuthorization(new AuthData("token378", "chloe"));
        // make sure it is there
        assertNotNull(data.getAuthorization("token378"));
    }

    @Test
    void multipleAuth() throws DataException {
        // add a token
        data.makeAuthorization(new AuthData("token378", "chloe"));
        // adding same token should fail
        assertThrows(DataException.class, () ->
                data.makeAuthorization(new AuthData("token378", "chloe")));
    }






    //getAuthorizations: need Pos and
    @Test
    void getAuthorizationPas() throws DataException {
        // add a token first
        data.makeAuthorization(new AuthData("token578", "chloe"));
        // make sure we can get it back
        assertNotNull(data.getAuthorization("token578"));
    }

    @Test
    void getAuthorizationFail() throws DataException {
        // token that doesnt exist should return null
        assertNull(data.getAuthorization("failtoken"));
    }

    //deleteAuthorization: need Pos and

    @Test
    void deleteAuthorizationPos() throws DataException {
        // add a token then delete it
        data.makeAuthorization(new AuthData("token578", "chloe"));
        data.deleteAuthorization("token578");
        // should be gone now
        assertNull(data.getAuthorization("token578"));
    }

    @Test
    void deleteAuthorizationNeg() {
        // deleting token that doesnt exist should not throw
        assertDoesNotThrow(() -> data.deleteAuthorization("failtoken"));
    }

    //clear: pos
    @Test
    void clearPos() throws DataException {
        // add some stuff
        data.makeChessUser(new UserData("chloe", "pass", "chloe@email.com"));
        data.createGame(new GameData(0, null, null, "testGame", new ChessGame()));
        // wipe it all
        data.clear();
        // everything should be gone
        assertNull(data.getUser("chloe"));
        assertEquals(0, data.listGames().size());
    }

}