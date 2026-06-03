package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

//I am going to need psotive and negative tests for these all

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;



    //This is to setup the tests
    @BeforeAll
    public static void init() {
        //random port
        server = new Server();
        var port = server.run(0);
        System.out.println("Test server on port " + port + " :)");

        facade = new ServerFacade(port);

    }

    //this will stop the sever
    @AfterAll
    static void stopTheServer() {
        server.stop();

        System.out.println("The Server has stopped");

    }

    //THis will clear everything
    @BeforeEach
    void clearItAll() throws Exception {
        facade.clear();
    }

    // register
    @Test
    void registerPositive() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        var chloesUsername = theAuth.username();
        assertTrue(myChessToken.length() > 10);
        assertEquals("chloe", chloesUsername);

    }



    @Test
    void itIsTheSameRegisterNegative() throws Exception {
        facade.register("chloe", "mypassword", "chloe@email.com");
        var sameUsername = "chloe";
        var differentPassword = "newpassword";
        var differentEmail = "different@email.com";

        assertThrows(Exception.class, () -> //chloe is alreayd there = fail need diff inform
                facade.register(sameUsername, differentPassword, differentEmail));

    }

    // These will be the test for the logout
    @Test
    void logoutPersonPositive() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();

        assertDoesNotThrow(() -> facade.logout(myChessToken));

    }

    @Test
    void thereIsBadTokenNegative() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var chloesOldToken = theAuth.authToken();
        facade.logout(chloesOldToken); //logout with vaild


        assertThrows(Exception.class, () -> //dead now = fail
                facade.logout(chloesOldToken));
    }



    //these will be my login tests
    @Test
    void loginPersonPositive() throws Exception {
        facade.register("chloe", "mypassword", "chloe@email.com");
        var chloeLogin = facade.login("chloe", "mypassword");
        var myChessToken = chloeLogin.authToken();
        var chloesUsername = chloeLogin.username();

        assertTrue(myChessToken.length() > 10);

        assertEquals("chloe", chloesUsername);
    }

    @Test
    void loginBadPasswordNegative() throws Exception {
        var userThatDoesntExist = "chloe";
        var somePassword = "mypassword";


        assertThrows(Exception.class, () -> //not register = fail
                facade.login(userThatDoesntExist, somePassword));
    }



    // these will be for listGames
    @Test
    void listTheGamesPositive() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        var firstGame = "my game number 1";
        var secondGame = "my game number 2";

        facade.createGame(firstGame, myChessToken);
        facade.createGame(secondGame, myChessToken);
        GameData[] chloesGames = facade.listGames(myChessToken);
        int numberOfGames = chloesGames.length;

        assertEquals(2, numberOfGames);
        assertEquals(firstGame, chloesGames[0].gameName());
        assertEquals(secondGame, chloesGames[1].gameName());
    }

    @Test
    void listTheGamesNegative() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        facade.logout(myChessToken);


        assertThrows(Exception.class, () -> //cant create if logged out
                facade.listGames(myChessToken));
    }



    // This will be the tests for joinGame
    @Test
    void joinAGamePositive() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        var chloesGameID = facade.createGame("my game number 1", myChessToken);
        var chloesColor = "BLACK";


        assertDoesNotThrow(() -> facade.joinGame(chloesGameID, chloesColor, myChessToken));
    }


    @Test

    void joinGameGameDoesNotExistNegative() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        var gameThatDoesntExist = 12345;
        var chloesColor = "BLACK";

        assertThrows(Exception.class, () -> //should fail because 12345 was never created
                facade.joinGame(gameThatDoesntExist, chloesColor, myChessToken));

    }
    // These will be for createGame
    @Test
    void createANewGamePositive() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        var gameName = "my chess game";

        int chloesGameID = facade.createGame(gameName, myChessToken);
        assertTrue(chloesGameID > 0);
    }


    @Test
    void badAuthForCreateGameNegative() throws Exception {
        var theAuth = facade.register("chloe", "mypassword", "chloe@email.com");
        var myChessToken = theAuth.authToken();
        facade.logout(myChessToken);

        assertThrows(Exception.class, () -> //logged out should fail
                facade.createGame("my chess game", myChessToken));
    }

    // NEed to clear
    @Test
    void clear() throws Exception {
        facade.register("chloe", "mypassword", "chloe@email.com");
        assertDoesNotThrow(() -> facade.clear());

        assertDoesNotThrow(() ->
                facade.register("chloe", "mypassword", "chloe@email.com"));
    }

}
