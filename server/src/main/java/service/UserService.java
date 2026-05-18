package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

//This handles everything user related
public class UserService {
    private final DataAccess dataAccess;

    //This is how I get my data access
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //This registers a new user and gives them an auth token
    public AuthData register(UserData user) throws DataAccessException, AlreadyTakenException {
        //Can't register if username is already taken
        if (dataAccess.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        //Create the user in memory
        dataAccess.createUser(user);
        //Make a unique token and store it
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        dataAccess.createAuth(auth);
        return auth;
    }
}