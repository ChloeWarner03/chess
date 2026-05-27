package dataaccess;

//my imports
import model.UserData;
import org.junit.jupiter.api.*;

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
}