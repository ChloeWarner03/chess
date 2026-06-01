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
        // Start the server on a random port so we don't conflict with anything
        server = new Server();
        var port = server.run(0);
        System.out.println("My test server started on port " + port + " :)");
        facade = new ServerFacade(port);
    }
    //this will stop the sever
    @AfterAll
    static void stopServer() {
        server.stop();
        System.out.println("The test has stopped");
    }

    //THis will clear everything
    @BeforeEach
    void clearEverything() throws Exception {
        facade.clear();
    }



    //register
    @Test
    @DisplayName("Getting Registered was a successs")
    void registerSuccess() throws Exception {
        AuthData auth = facade.register("chloe", "password123", "chloe@email.com");

        // We should get back a real auth token
        assertNotNull(auth.authToken(), "Authentication token cannot be null");
        assertTrue(auth.authToken().length() > 10, "Authentication token whould be at least 10 in length");
        assertEquals("chloe", auth.username(), "Username needs to match what was created when registered");
    }

}
