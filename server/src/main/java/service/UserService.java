package service;

//These are my imports
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;
//fizing things

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

    //This logs in an existing user and gives them a new auth token
    public AuthData login(UserData user) throws DataAccessException, UnauthorizedException {
        //Check if user exists and password matches
        UserData existing = dataAccess.getUser(user.username());
        if (existing == null || !existing.password().equals(user.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        //Make a new token and store it
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        dataAccess.createAuth(auth);
        return auth;
    }

    //This logs out a user by deleting their auth token
    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        //Check if the auth token is valid
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Not logged in");
        }
        //Delete the auth token
        dataAccess.deleteAuth(authToken);
    }
}